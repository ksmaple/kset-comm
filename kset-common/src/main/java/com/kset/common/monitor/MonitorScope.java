package com.kset.common.monitor;


public final class MonitorScope implements AutoCloseable {

    private final TraceSnapshot previous;

    public MonitorScope(TraceSnapshot previous) {
        this.previous = previous;
    }

    @Override
    public void close() {
        if (previous != null) {
            Monitor.restore(previous);
        } else {
            Monitor.clear();
        }
    }
}
