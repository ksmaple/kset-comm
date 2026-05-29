package com.kset.common.monitor.reporter;

import com.kset.common.monitor.facade.MetricKind;

/**
 * 客户端指标预聚合 SPI（降低 Log 后端刷屏）。
 */
public interface MetricAggregator {

    void record(String name, long value, MetricKind kind);

    /**
     * 取出并清空当前窗口内的聚合快照（用于周期性刷日志）。
     */
    String flushSummary();
}
