package com.kset.common.monitor.dubbo;

import com.kset.common.monitor.DubboAttachmentAccessor;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.RpcContext;

/**
 * Dubbo invocation + RpcContext attachment 访问器。
 */
public final class DubboInvocationAttachments implements DubboAttachmentAccessor {

    private final Invocation invocation;
    private final boolean providerSide;

    public DubboInvocationAttachments(Invocation invocation, boolean providerSide) {
        this.invocation = invocation;
        this.providerSide = providerSide;
    }

    @Override
    public String getAttachment(String key) {
        if (providerSide) {
            String value = invocation.getAttachment(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
            return RpcContext.getServiceContext().getAttachment(key);
        }
        return invocation.getAttachment(key);
    }

    @Override
    public void setAttachment(String key, String value) {
        invocation.setAttachment(key, value);
    }
}
