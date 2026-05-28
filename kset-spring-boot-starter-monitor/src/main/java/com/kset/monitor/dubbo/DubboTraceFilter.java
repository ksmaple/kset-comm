package com.kset.monitor.dubbo;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.common.monitor.KsetMonitor;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

/**
 * Dubbo TraceId / 灰度标签透传 Filter（经 {@link KsetMonitor} 门面）。
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
        String defaultGray = properties.getDubbo().getDefaultGrayTag();
        if (context.isConsumerSide()) {
            KsetMonitor.bindDubboConsumer(new DubboInvocationAttachments(invocation, false), defaultGray);
        } else if (context.isProviderSide()) {
            KsetMonitor.bindDubboProvider(new DubboInvocationAttachments(invocation, true), defaultGray);
        }

        try {
            return invoker.invoke(invocation);
        } finally {
            if (context.isProviderSide()) {
                KsetMonitor.clear();
            }
        }
    }
}
