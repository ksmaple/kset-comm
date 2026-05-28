package com.kset.common.monitor;

import com.kset.common.trace.TraceHeaders;

/**
 * Gateway 入站 Trace 解析结果（供下游 Header 与 Reactor Context 使用）。
 */
public final class GatewayTraceBinding {

    private final String traceId;
    private final String spanId;
    private final String traceHeaderName;

    public GatewayTraceBinding(String traceId, String spanId, String traceHeaderName) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.traceHeaderName = traceHeaderName;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getTraceHeaderName() {
        return traceHeaderName;
    }

    public String getSpanIdHeaderName() {
        return TraceHeaders.SPAN_ID_HEADER;
    }
}
