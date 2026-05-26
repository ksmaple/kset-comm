package com.kset.common.utils.thread;

import com.kset.common.trace.TraceHeaders;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * 基于 SLF4J MDC 的链路上下文适配器（反射实现，不强制依赖 slf4j）。
 *
 * <p>自动检测常见的 traceId key：traceId、X-B3-TraceId、trace_id、tid、requestId。
 * 若项目未引入 slf4j，所有方法静默返回 null 或空操作，不会抛出异常。</p>
 */
@Slf4j
public class MdcThreadPoolTraceAdapter implements ThreadPoolTraceAdapter {

    private static final List<String> DEFAULT_TRACE_KEYS = Arrays.asList(
            TraceHeaders.TRACE_ID_KEY, TraceHeaders.TRACE_ID_HEADER,
            "X-B3-TraceId", "trace_id", "tid", "requestId"
    );

    private final List<String> traceKeys;
    private final boolean mdcAvailable;

    public MdcThreadPoolTraceAdapter() {
        this(DEFAULT_TRACE_KEYS);
    }

    public MdcThreadPoolTraceAdapter(List<String> traceKeys) {
        this.traceKeys = traceKeys != null ? traceKeys : DEFAULT_TRACE_KEYS;
        boolean available = false;
        try {
            Class.forName("org.slf4j.MDC");
            available = true;
        } catch (ClassNotFoundException e) {
            log.warn("[MdcThreadPoolTraceAdapter] SLF4J MDC not found, traceId propagation disabled");
        }
        this.mdcAvailable = available;
    }

    @Override
    public String getTraceId() {
        if (!mdcAvailable) {
            return null;
        }
        try {
            Class<?> mdcClass = Class.forName("org.slf4j.MDC");
            for (String key : traceKeys) {
                Object result = mdcClass.getMethod("get", String.class).invoke(null, key);
                if (result != null) {
                    return (String) result;
                }
            }
        } catch (Exception e) {
            log.debug("[MdcThreadPoolTraceAdapter] Failed to get traceId from MDC: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void setTraceId(String traceId) {
        if (!mdcAvailable || traceId == null) {
            return;
        }
        try {
            Class<?> mdcClass = Class.forName("org.slf4j.MDC");
            mdcClass.getMethod("put", String.class, String.class).invoke(null, traceKeys.get(0), traceId);
        } catch (Exception e) {
            log.debug("[MdcThreadPoolTraceAdapter] Failed to put traceId to MDC: {}", e.getMessage());
        }
    }

    @Override
    public void clear() {
        if (!mdcAvailable) {
            return;
        }
        try {
            Class<?> mdcClass = Class.forName("org.slf4j.MDC");
            for (String key : traceKeys) {
                mdcClass.getMethod("remove", String.class).invoke(null, key);
            }
        } catch (Exception e) {
            log.debug("[MdcThreadPoolTraceAdapter] Failed to remove traceId from MDC: {}", e.getMessage());
        }
    }
}
