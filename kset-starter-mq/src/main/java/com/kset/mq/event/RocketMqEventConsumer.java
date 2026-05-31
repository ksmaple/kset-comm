package com.kset.mq.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kset.common.event.EventHandler;
import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.MonitorScope;
import com.kset.common.monitor.TraceSnapshot;
import com.kset.common.monitor.facade.MonitorTransaction;
import com.kset.common.trace.TraceHeaders;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.core.RocketMQListener;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

@RocketMQMessageListener(
        endpoints = "${rocketmq.producer.endpoints:}",
        topic = "${rocketmq.producer.topic:${spring.application.name:kset}-event}",
        tag = "*",
        consumerGroup = "${spring.application.name:kset}-event-consumer"
)
public class RocketMqEventConsumer implements RocketMQListener {

    private final List<EventHandler<?>> handlers;
    private final ObjectMapper objectMapper;

    public RocketMqEventConsumer(List<EventHandler<?>> handlers, ObjectMapper objectMapper) {
        this.handlers = List.copyOf(handlers);
        this.objectMapper = objectMapper;
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        String eventType = messageView.getProperties().get(RocketMqEventHeaders.EVENT_TYPE);
        try {
            Class<?> eventClass = Class.forName(eventType);
            Object event = objectMapper.readValue(bytes(messageView.getBody()), eventClass);
            try (MonitorScope ignored = Monitor.openScope(traceSnapshot(messageView))) {
                dispatch(eventType, event);
            }
            return ConsumeResult.SUCCESS;
        } catch (Throwable throwable) {
            MonitorTransaction transaction = RocketMqEventMonitorSupport.beginConsume(eventType, null);
            try {
                RocketMqEventMonitorSupport.fail(transaction, throwable, "consume", eventType);
            } finally {
                RocketMqEventMonitorSupport.close(transaction);
            }
            return ConsumeResult.FAILURE;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void dispatch(String eventType, T event) {
        for (EventHandler<?> handler : handlers) {
            if (handler.eventType().isAssignableFrom(event.getClass())) {
                MonitorTransaction transaction = RocketMqEventMonitorSupport.beginConsume(eventType, handler);
                try {
                    ((EventHandler<T>) handler).handle(event);
                    RocketMqEventMonitorSupport.success(transaction);
                } catch (RuntimeException | Error e) {
                    RocketMqEventMonitorSupport.fail(transaction, e, "consume", eventType);
                    throw e;
                } finally {
                    RocketMqEventMonitorSupport.close(transaction);
                }
            }
        }
    }

    private TraceSnapshot traceSnapshot(MessageView messageView) {
        Map<String, String> properties = messageView.getProperties();
        String traceId = properties.get(TraceHeaders.TRACE_ID_KEY);
        if (traceId == null || traceId.isBlank()) {
            traceId = Monitor.generateTraceId();
        }
        String spanId = properties.get(TraceHeaders.SPAN_ID_KEY);
        if (spanId == null || spanId.isBlank()) {
            spanId = Monitor.generateSpanId();
        }
        return new TraceSnapshot(traceId, spanId, properties.get(TraceHeaders.GRAY_TAG_KEY));
    }

    private byte[] bytes(ByteBuffer buffer) {
        ByteBuffer duplicate = buffer.asReadOnlyBuffer();
        byte[] bytes = new byte[duplicate.remaining()];
        duplicate.get(bytes);
        return bytes;
    }
}
