package com.kset.common.monitor.backend;

import com.kset.common.monitor.facade.CompletedTransaction;
import com.kset.common.monitor.facade.MonitorEvent;
import com.kset.common.monitor.facade.MonitorMetric;
import com.kset.common.monitor.facade.MonitorStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认本地日志后端：通过 SLF4J 输出到应用本地日志（Logback），慢事务/失败事务 WARN。
 */
public final class LogBackend implements MonitorBackend {

    private static final Logger log = LoggerFactory.getLogger(LogBackend.class);

    public static final long DEFAULT_TRANSACTION_WARN_MS = 500L;

    private final long transactionWarnMs;

    public LogBackend() {
        this(DEFAULT_TRANSACTION_WARN_MS);
    }

    public LogBackend(long transactionWarnMs) {
        this.transactionWarnMs = transactionWarnMs;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void completeTransaction(CompletedTransaction transaction) {
        String traceId = transaction.traceId().orElse("-");
        if (transaction.durationMs() >= transactionWarnMs
                || transaction.status() == MonitorStatus.FAIL) {
            log.warn("monitor-tx type={} name={} durationMs={} status={} parentType={} traceId={} data={}",
                    transaction.type(),
                    transaction.name(),
                    transaction.durationMs(),
                    transaction.status(),
                    transaction.parentType().orElse("-"),
                    traceId,
                    transaction.data());
        } else if (log.isDebugEnabled()) {
            log.debug("monitor-tx type={} name={} durationMs={} traceId={}",
                    transaction.type(), transaction.name(), transaction.durationMs(), traceId);
        }
    }

    @Override
    public void logEvent(MonitorEvent event) {
        String traceId = event.traceId().orElse("-");
        if (event.status() == MonitorStatus.FAIL) {
            log.warn("monitor-event type={} name={} status={} traceId={} data={}",
                    event.type(), event.name(), event.status(), traceId, event.data());
        } else {
            log.info("monitor-event type={} name={} status={} traceId={} data={}",
                    event.type(), event.name(), event.status(), traceId, event.data());
        }
    }

    @Override
    public void logMetric(MonitorMetric metric) {
        if (log.isDebugEnabled()) {
            log.debug("monitor-metric name={} value={} kind={} traceId={}",
                    metric.name(), metric.value(), metric.kind(), metric.traceId().orElse("-"));
        }
    }

    @Override
    public void logError(Throwable throwable, String message) {
        logError("-", throwable, message);
    }

    @Override
    public void logError(String traceId, Throwable throwable, String message) {
        log.error("monitor-error traceId={} message={}", traceId != null ? traceId : "-", message, throwable);
    }
}
