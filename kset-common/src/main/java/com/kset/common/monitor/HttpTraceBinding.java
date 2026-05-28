package com.kset.common.monitor;

import com.kset.common.trace.TraceHeaders;

/**
 * Servlet HTTP 入站链路绑定结果（已写入 MDC）。
 */
public final class HttpTraceBinding {

    private final String traceId;
    private final String spanId;

    public HttpTraceBinding(String traceId, String spanId) {
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getTraceIdHeaderName() {
        return TraceHeaders.TRACE_ID_HEADER;
    }

    public String getSpanIdHeaderName() {
        return TraceHeaders.SPAN_ID_HEADER;
    }
}
