package com.kset.cache.core;

import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.facade.MetricKind;

import java.util.concurrent.atomic.AtomicLong;

public class KsetCacheMetricCollector {

    private final AtomicLong l1Hits = new AtomicLong();
    private final AtomicLong l2Hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong loads = new AtomicLong();
    private final AtomicLong puts = new AtomicLong();
    private final AtomicLong evicts = new AtomicLong();
    private final AtomicLong errors = new AtomicLong();

    public void hit(KsetCacheLayer layer) {
        if (layer == KsetCacheLayer.L1) {
            increment(l1Hits, "kset.cache.l1.hit");
        } else if (layer == KsetCacheLayer.L2) {
            increment(l2Hits, "kset.cache.l2.hit");
        }
    }

    public void miss() {
        increment(misses, "kset.cache.miss");
    }

    public void load() {
        increment(loads, "kset.cache.load");
    }

    public void put() {
        increment(puts, "kset.cache.put");
    }

    public void evict() {
        increment(evicts, "kset.cache.evict");
    }

    public void error() {
        increment(errors, "kset.cache.error");
    }

    public KsetCacheMetrics snapshot() {
        return new KsetCacheMetrics(
                l1Hits.get(),
                l2Hits.get(),
                misses.get(),
                loads.get(),
                puts.get(),
                evicts.get(),
                errors.get());
    }

    private static void increment(AtomicLong counter, String metricName) {
        counter.incrementAndGet();
        Monitor.logMetric(metricName, 1, MetricKind.COUNT);
    }
}
