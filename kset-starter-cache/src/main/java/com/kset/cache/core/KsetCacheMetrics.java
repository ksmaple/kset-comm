package com.kset.cache.core;

public record KsetCacheMetrics(
        long l1Hits,
        long l2Hits,
        long misses,
        long loads,
        long puts,
        long evicts,
        long errors
) {

    public long hits() {
        return l1Hits + l2Hits;
    }

    public long requests() {
        return hits() + misses;
    }
}
