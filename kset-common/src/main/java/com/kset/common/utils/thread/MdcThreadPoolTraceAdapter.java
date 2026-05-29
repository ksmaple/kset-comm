package com.kset.common.utils.thread;

import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.TraceSnapshot;


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
