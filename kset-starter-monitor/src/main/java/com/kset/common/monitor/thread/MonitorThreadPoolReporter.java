package com.kset.common.monitor.thread;

import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.TraceSnapshot;
import com.kset.common.monitor.facade.MetricKind;
import com.kset.common.monitor.facade.MonitorStatus;
import com.kset.common.monitor.facade.MonitorTypes;
import com.kset.common.utils.thread.ThreadPoolMetrics;
import com.kset.common.utils.thread.ThreadPoolReporter;

/**
 * Bridges KSet thread-pool lifecycle callbacks to the unified monitor facade.
 */
public class MonitorThreadPoolReporter implements ThreadPoolReporter {

    public MonitorThreadPoolReporter() {
    }

    @Override
    public void onTaskSubmitted(String poolName, String traceId, Runnable task) {
        withTrace(traceId, () -> Monitor.logMetric(metricName(poolName, "submitted"), 1, MetricKind.COUNT));
    }

    @Override
    public void onTaskStarted(String poolName, String traceId, Runnable task, long waitTimeMs) {
        withTrace(traceId, () -> {
            Monitor.logMetric(metricName(poolName, "queue.wait"), waitTimeMs, MetricKind.DURATION);
            Monitor.logEvent(MonitorTypes.THREAD_POOL, poolName + ".queueWait",
                    MonitorStatus.SUCCESS, "waitTimeMs=" + waitTimeMs);
        });
    }

    @Override
    public void onTaskCompleted(String poolName, String traceId, Runnable task, long executionTimeMs, boolean success) {
        withTrace(traceId, () -> {
            Monitor.logMetric(metricName(poolName, "execution"), executionTimeMs, MetricKind.DURATION);
            if (!success) {
                Monitor.logEvent(MonitorTypes.THREAD_POOL, poolName + ".task", MonitorStatus.FAIL,
                        "executionTimeMs=" + executionTimeMs);
                return;
            }
            Monitor.logEvent(MonitorTypes.THREAD_POOL, poolName + ".task", MonitorStatus.SUCCESS,
                    "executionTimeMs=" + executionTimeMs);
        });
    }

    @Override
    public void onTaskRejected(String poolName, String traceId, Runnable task, String policyName) {
        withTrace(traceId, () -> Monitor.logEvent(MonitorTypes.THREAD_POOL, poolName + ".rejected",
                MonitorStatus.FAIL, "policy=" + policyName));
    }

    @Override
    public void onAutoTuned(String poolName, String traceId, String action, ThreadPoolMetrics metrics) {
        withTrace(traceId, () -> Monitor.logEvent(MonitorTypes.THREAD_POOL, poolName + ".autoTuned",
                MonitorStatus.SUCCESS, "action=" + action));
    }

    @Override
    public void onError(String poolName, String traceId, Runnable task, Throwable t) {
        withTrace(traceId, () -> Monitor.logError(t, poolName + ".task"));
    }

    @Override
    public void onMetricsReport(String poolName, String traceId, ThreadPoolMetrics metrics) {
        withTrace(traceId, () -> {
            Monitor.logMetric(metricName(poolName, "active"), metrics.getActiveCount(), MetricKind.COUNT);
            Monitor.logMetric(metricName(poolName, "queue.size"), metrics.getQueueSize(), MetricKind.COUNT);
            Monitor.logMetric(metricName(poolName, "completed"), metrics.getCompletedTasks(), MetricKind.COUNT);
            Monitor.logMetric(metricName(poolName, "failed"), metrics.getFailedTasks(), MetricKind.COUNT);
            Monitor.logMetric(metricName(poolName, "rejected"), metrics.getRejectedTasks(), MetricKind.COUNT);
        });
    }

    private static void withTrace(String traceId, Runnable action) {
        if (traceId == null || traceId.isBlank()) {
            action.run();
            return;
        }
        TraceSnapshot previous = Monitor.capture();
        try {
            Monitor.setTraceId(traceId);
            action.run();
        } finally {
            Monitor.restore(previous);
        }
    }

    private static String metricName(String poolName, String metric) {
        return "thread.pool." + poolName + "." + metric;
    }
}
