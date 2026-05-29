package com.kset.common.utils.thread;

import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.TraceSnapshot;

/**
 * 基于 {@link Monitor} 门面的链路上下文适配器（线程池跨线程传播）。
 */
public class MdcThreadPoolTraceAdapter implements ThreadPoolTraceAdapter {

    @Override
    public String getTraceId() {
        return Monitor.currentTraceId().orElse(null);
    }

    @Override
    public void setTraceId(String traceId) {
        if (traceId != null) {
            Monitor.setTraceId(traceId);
        }
    }

    @Override
    public void clear() {
        Monitor.clear();
    }

    /**
     * 捕获完整快照（含 spanId/grayTag），供线程池任务恢复。
     */
    public TraceSnapshot captureSnapshot() {
        return Monitor.capture();
    }

    /**
     * 从快照恢复上下文。
     */
    public void restoreSnapshot(TraceSnapshot snapshot) {
        Monitor.restore(snapshot);
    }
}
