package com.kset.mq.event;

import com.kset.common.event.EventHandler;
import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.facade.MonitorStatus;
import com.kset.common.monitor.facade.MonitorTransaction;
import com.kset.common.monitor.facade.MonitorTypes;
import com.kset.common.monitor.internal.NoOpMonitorTransaction;

final class RocketMqEventMonitorSupport {

    private static final String CHANNEL = "rocketmq";

    private RocketMqEventMonitorSupport() {
    }

    static MonitorTransaction beginPublish(String mode, Object event) {
        return begin("event.publish." + mode, eventType(event), null);
    }

    static MonitorTransaction beginConsume(String eventType, EventHandler<?> handler) {
        return begin("event.consume", eventType, handler);
    }

    static void success(MonitorTransaction transaction) {
        try {
            transaction.setStatus(MonitorStatus.SUCCESS);
        } catch (RuntimeException | Error ignored) {
            // 监控不能影响事件主流程。
        }
    }

    static void addData(MonitorTransaction transaction, String key, String value) {
        try {
            transaction.addData(key, value);
        } catch (RuntimeException | Error ignored) {
            // 监控不能影响事件主流程。
        }
    }

    static void close(MonitorTransaction transaction) {
        try {
            transaction.close();
        } catch (RuntimeException | Error ignored) {
            // 监控不能影响事件主流程。
        }
    }

    static void fail(MonitorTransaction transaction, Throwable throwable, String action, String eventType) {
        try {
            transaction.setStatus(throwable);
            transaction.addData("action", action);
            Monitor.logError(throwable, action + " rocketmq event failed: " + eventType);
        } catch (RuntimeException | Error ignored) {
            // 监控不能覆盖原始事件异常。
        }
    }

    static String eventType(Object event) {
        return event == null ? "null" : event.getClass().getName();
    }

    private static MonitorTransaction begin(String name, String eventType, EventHandler<?> handler) {
        try {
            MonitorTransaction transaction = Monitor.newTransaction(MonitorTypes.MQ, name);
            transaction.addData("channel", CHANNEL);
            transaction.addData("eventType", eventType);
            if (handler != null) {
                transaction.addData("handler", handler.getClass().getName());
            }
            return transaction;
        } catch (RuntimeException | Error ignored) {
            return new NoOpMonitorTransaction(MonitorTypes.MQ, name);
        }
    }
}
