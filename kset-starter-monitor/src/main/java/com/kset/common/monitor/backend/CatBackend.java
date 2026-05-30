package com.kset.common.monitor.backend;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.kset.common.monitor.facade.CompletedTransaction;
import com.kset.common.monitor.facade.MetricKind;
import com.kset.common.monitor.facade.MonitorEvent;
import com.kset.common.monitor.facade.MonitorMetric;
import com.kset.common.monitor.facade.MonitorStatus;

/**
 * CAT 后端实现；仅在显式配置 {@code kset.monitor.backend=cat} 时由 starter 装配。
 */
public final class CatBackend implements MonitorBackend {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void completeTransaction(CompletedTransaction transaction) {
        Transaction catTransaction = Cat.newTransaction(transaction.type(), transaction.name());
        catTransaction.setDurationInMillis(transaction.durationMs());
        transaction.data().forEach(catTransaction::addData);
        if (transaction.status() == MonitorStatus.FAIL) {
            catTransaction.setStatus("FAIL");
        } else {
            catTransaction.setStatus(Message.SUCCESS);
        }
        catTransaction.complete();
    }

    @Override
    public void logEvent(MonitorEvent event) {
        Cat.logEvent(event.type(), event.name(), toCatStatus(event.status()), event.data());
    }

    @Override
    public void logMetric(MonitorMetric metric) {
        if (metric.kind() == MetricKind.DURATION) {
            Cat.logMetricForDuration(metricName(metric), metric.value());
        } else {
            Cat.logMetricForCount(metricName(metric), Math.toIntExact(metric.value()));
        }
    }

    @Override
    public void logError(Throwable throwable, String message) {
        Cat.logError(message, throwable);
    }

    @Override
    public void logError(String traceId, Throwable throwable, String message) {
        Cat.logError(messageWithTrace(traceId, message), throwable);
    }

    private static String toCatStatus(MonitorStatus status) {
        return status == MonitorStatus.FAIL ? "FAIL" : Message.SUCCESS;
    }

    private static String metricName(MonitorMetric metric) {
        return metric.traceId()
                .filter(traceId -> !traceId.isBlank() && !"-".equals(traceId))
                .map(traceId -> metric.name() + ".trace." + traceId)
                .orElse(metric.name());
    }

    private static String messageWithTrace(String traceId, String message) {
        if (traceId == null || traceId.isBlank() || "-".equals(traceId)) {
            return message;
        }
        return message + " traceId=" + traceId;
    }
}
