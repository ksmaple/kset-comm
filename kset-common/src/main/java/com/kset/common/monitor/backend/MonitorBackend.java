package com.kset.common.monitor.backend;

import com.kset.common.monitor.facade.CompletedTransaction;
import com.kset.common.monitor.facade.MonitorEvent;
import com.kset.common.monitor.facade.MonitorMetric;

/**
 * 监控后端策略（CAT / SkyWalking / Prometheus / Log 等实现）。
 */
public interface MonitorBackend {

    boolean isEnabled();

    void completeTransaction(CompletedTransaction transaction);

    void logEvent(MonitorEvent event);

    void logMetric(MonitorMetric metric);

    void logError(Throwable throwable, String message);

    /**
     * 带 traceId 的异常上报；默认实现忽略 traceId，由 {@link LogBackend} 等本地后端覆盖。
     */
    default void logError(String traceId, Throwable throwable, String message) {
        logError(throwable, message);
    }
}
