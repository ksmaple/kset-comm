package com.kset.common.monitor;

import com.kset.common.monitor.internal.NoOpMonitorFacade;

import java.util.Objects;
import java.util.Optional;

/**
 * 全链路监控统一静态入口；框架组件与业务代码均通过本类访问链路上下文。
 */
public final class KsetMonitor {

    private static volatile KsetMonitorFacade facade = new NoOpMonitorFacade();

    private KsetMonitor() {
    }

    public static void install(KsetMonitorFacade newFacade) {
        facade = Objects.requireNonNull(newFacade, "facade");
    }

    public static KsetMonitorFacade facade() {
        return facade;
    }

    public static Optional<String> currentTraceId() {
        return facade.currentTraceId();
    }

    public static Optional<String> currentSpanId() {
        return facade.currentSpanId();
    }

    public static Optional<String> currentGrayTag() {
        return facade.currentGrayTag();
    }

    public static String generateTraceId() {
        return facade.generateTraceId();
    }

    public static String generateSpanId() {
        return facade.generateSpanId();
    }

    public static HttpTraceBinding bindHttpIncoming(String incomingTraceId) {
        return facade.bindHttpIncoming(incomingTraceId);
    }

    public static void bindHttpGrayTag(String incomingGrayTag, String defaultGray) {
        facade.bindHttpGrayTag(incomingGrayTag, defaultGray);
    }

    public static void clearHttpGrayTag() {
        facade.clearHttpGrayTag();
    }

    public static void bindDubboConsumer(DubboAttachmentAccessor attachments, String defaultGray) {
        facade.bindDubboConsumer(attachments, defaultGray);
    }

    public static void bindDubboProvider(DubboAttachmentAccessor attachments, String defaultGray) {
        facade.bindDubboProvider(attachments, defaultGray);
    }

    public static GatewayTraceBinding resolveGatewayTrace(String incomingTraceId, String traceHeaderName) {
        return facade.resolveGatewayTrace(incomingTraceId, traceHeaderName);
    }

    public static Object putReactorContext(Object context, String traceId, String grayTag) {
        return facade.putReactorContext(context, traceId, grayTag);
    }

    public static Optional<String> getFromReactor(Object contextView, String key) {
        return facade.getFromReactor(contextView, key);
    }

    public static void setTraceId(String traceId) {
        facade.setTraceId(traceId);
    }

    public static void setSpanId(String spanId) {
        facade.setSpanId(spanId);
    }

    public static void setGrayTag(String grayTag) {
        facade.setGrayTag(grayTag);
    }

    public static void clear() {
        facade.clear();
    }

    public static TraceSnapshot capture() {
        return facade.capture();
    }

    public static void restore(TraceSnapshot snapshot) {
        facade.restore(snapshot);
    }

    public static MonitorScope openScope(TraceSnapshot snapshot) {
        TraceSnapshot previous = capture();
        restore(snapshot);
        return new MonitorScope(previous);
    }

    public static void recordSlowEvent(String type, long costMs, String message) {
        facade.recordSlowEvent(type, costMs, message);
    }
}
