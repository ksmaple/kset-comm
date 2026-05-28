package com.kset.cloud.trace;

import com.kset.common.monitor.KsetMonitor;
import com.kset.common.trace.TraceHeaders;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Optional;

/**
 * 跨组件链路上下文（兼容层，委托 {@link KsetMonitor} 门面）。
 *
 * @deprecated 请使用 {@link KsetMonitor}，本类保留 1～2 个版本兼容。
 */
@Deprecated
public final class TraceContext {

    public static final String TRACE_ID_KEY = TraceHeaders.TRACE_ID_KEY;
    public static final String SPAN_ID_KEY = TraceHeaders.SPAN_ID_KEY;
    public static final String GRAY_TAG_KEY = TraceHeaders.GRAY_TAG_KEY;
    public static final String TRACE_ID_HEADER = TraceHeaders.TRACE_ID_HEADER;
    public static final String SPAN_ID_HEADER = TraceHeaders.SPAN_ID_HEADER;
    public static final String GRAY_TAG_HEADER = TraceHeaders.GRAY_TAG_HEADER;

    private TraceContext() {
    }

    public static String generateTraceId() {
        return KsetMonitor.generateTraceId();
    }

    public static String generateSpanId() {
        return KsetMonitor.generateSpanId();
    }

    public static void setTraceId(String traceId) {
        KsetMonitor.setTraceId(traceId);
    }

    public static void setSpanId(String spanId) {
        KsetMonitor.setSpanId(spanId);
    }

    public static void setGrayTag(String grayTag) {
        KsetMonitor.setGrayTag(grayTag);
    }

    public static Optional<String> getTraceId() {
        return KsetMonitor.currentTraceId();
    }

    public static Optional<String> getGrayTag() {
        return KsetMonitor.currentGrayTag();
    }

    public static void clear() {
        KsetMonitor.clear();
    }

    public static Context putReactorContext(Context context, String traceId, String grayTag) {
        return (Context) KsetMonitor.putReactorContext(context, traceId, grayTag);
    }

    public static Optional<String> getFromReactor(ContextView contextView, String key) {
        return KsetMonitor.getFromReactor(contextView, key);
    }
}
