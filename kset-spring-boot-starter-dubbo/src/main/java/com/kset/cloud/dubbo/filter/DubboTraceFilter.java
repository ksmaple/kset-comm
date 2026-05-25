package com.kset.cloud.dubbo.filter;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.cloud.trace.TraceContext;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.MDC;

/**
 * Dubbo TraceId / 灰度标签透传 Filter
 */
public class DubboTraceFilter implements Filter {

    private final KsetCloudProperties properties;

    public DubboTraceFilter(KsetCloudProperties properties) {
        this.properties = properties;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!properties.getDubbo().isTracePropagationEnabled()) {
            return invoker.invoke(invocation);
        }

        RpcContext context = RpcContext.getServiceContext();
        if (context.isConsumerSide()) {
            propagateConsumer(invocation);
        } else if (context.isProviderSide()) {
            propagateProvider(invocation);
        }

        try {
            return invoker.invoke(invocation);
        } finally {
            if (context.isProviderSide()) {
                TraceContext.clear();
            }
        }
    }

    private void propagateConsumer(Invocation invocation) {
        String traceId = TraceContext.getTraceId().orElse(TraceContext.generateTraceId());
        String grayTag = TraceContext.getGrayTag().orElse(properties.getDubbo().getDefaultGrayTag());

        invocation.setAttachment(TraceContext.TRACE_ID_KEY, traceId);
        invocation.setAttachment(TraceContext.GRAY_TAG_KEY, grayTag);
        TraceContext.setTraceId(traceId);
        TraceContext.setGrayTag(grayTag);
    }

    private void propagateProvider(Invocation invocation) {
        String traceId = firstNonBlank(
                invocation.getAttachment(TraceContext.TRACE_ID_KEY),
                RpcContext.getServiceContext().getAttachment(TraceContext.TRACE_ID_KEY));
        String grayTag = firstNonBlank(
                invocation.getAttachment(TraceContext.GRAY_TAG_KEY),
                RpcContext.getServiceContext().getAttachment(TraceContext.GRAY_TAG_KEY));

        if (traceId == null) {
            traceId = TraceContext.generateTraceId();
        }
        if (grayTag == null) {
            grayTag = properties.getDubbo().getDefaultGrayTag();
        }

        TraceContext.setTraceId(traceId);
        TraceContext.setGrayTag(grayTag);
        MDC.put(TraceContext.TRACE_ID_KEY, traceId);
        MDC.put(TraceContext.GRAY_TAG_KEY, grayTag);
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
