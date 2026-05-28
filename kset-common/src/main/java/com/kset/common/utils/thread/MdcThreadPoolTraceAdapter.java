package com.kset.common.utils.thread;

import com.kset.common.monitor.KsetMonitor;
import com.kset.common.monitor.TraceSnapshot;

/**
 * 基于 {@link KsetMonitor} 门面的链路上下文适配器（线程池跨线程传播）。
 */
public class MdcThreadPoolTraceAdapter implements ThreadPoolTraceAdapter {

    @Override
    public String getTraceId() {
        return KsetMonitor.currentTraceId().orElse(null);
    }

    @Override
    public void setTraceId(String traceId) {
        if (traceId != null) {
            KsetMonitor.setTraceId(traceId);
        }
    }

    @Override
    public void clear() {
        KsetMonitor.clear();
    }

    /**
     * 捕获完整快照（含 spanId/grayTag），供线程池任务恢复。
     */
    public TraceSnapshot captureSnapshot() {
        return KsetMonitor.capture();
    }

    /**
     * 从快照恢复上下文。
     */
    public void restoreSnapshot(TraceSnapshot snapshot) {
        KsetMonitor.restore(snapshot);
    }
}
