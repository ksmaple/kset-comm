package com.kset.common.monitor;


public final class TraceSnapshot {

    private final String traceId;
    private final String spanId;
    private final String grayTag;

    public TraceSnapshot(String traceId, String spanId, String grayTag) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.grayTag = grayTag;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getGrayTag() {
        return grayTag;
    }
}
