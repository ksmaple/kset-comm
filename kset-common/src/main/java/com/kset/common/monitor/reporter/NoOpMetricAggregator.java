package com.kset.common.monitor.reporter;

import com.kset.common.monitor.facade.MetricKind;

/**
 * 不聚合指标（内置默认门面使用，指标仍由 {@link com.kset.common.monitor.backend.LogBackend} 逐条 DEBUG 输出）。
 */
public final class NoOpMetricAggregator implements MetricAggregator {

    @Override
    public void record(String name, long value, MetricKind kind) {
    }

    @Override
    public String flushSummary() {
        return "";
    }
}
