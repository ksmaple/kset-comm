package com.kset.common.monitor;

import java.util.Optional;

/**
 * 全链路监控门面：统一读写 TraceId / SpanId / 灰度标签（MDC 与 Reactor）。
 */
public interface KsetMonitorFacade {

    Optional<String> currentTraceId();

    Optional<String> currentSpanId();

    Optional<String> currentGrayTag();

    String generateTraceId();

    String generateSpanId();

    /**
     * Servlet 入站：绑定 traceId/spanId 至 MDC。
     *
     * @param incomingTraceId 请求头中的 traceId，可为 null/空则自动生成
     */
    HttpTraceBinding bindHttpIncoming(String incomingTraceId);

    /**
     * Servlet 灰度：绑定 grayTag 至 MDC。
     */
    void bindHttpGrayTag(String incomingGrayTag, String defaultGray);

    void clearHttpGrayTag();

    /**
     * Dubbo Consumer：从当前上下文或生成 ID，写入 attachment 并刷新 MDC。
     */
    void bindDubboConsumer(DubboAttachmentAccessor attachments, String defaultGray);

    /**
     * Dubbo Provider：从 attachment 恢复上下文至 MDC。
     */
    void bindDubboProvider(DubboAttachmentAccessor attachments, String defaultGray);

    /**
     * Gateway 入站：解析或生成 traceId/spanId（不写 MDC，由 Reactor Context 承载）。
     */
    GatewayTraceBinding resolveGatewayTrace(String incomingTraceId, String traceHeaderName);

    /**
     * 将 traceId/grayTag 写入 Reactor Context（Gateway 等响应式链路）。
     */
    Object putReactorContext(Object context, String traceId, String grayTag);

    Optional<String> getFromReactor(Object contextView, String key);

    void setTraceId(String traceId);

    void setSpanId(String spanId);

    void setGrayTag(String grayTag);

    void clear();

    TraceSnapshot capture();

    void restore(TraceSnapshot snapshot);

    /**
     * 慢调用/慢 SQL 等可观测事件（默认实现可仅打 DEBUG 日志，供后续接 APM）。
     */
    void recordSlowEvent(String type, long costMs, String message);
}
