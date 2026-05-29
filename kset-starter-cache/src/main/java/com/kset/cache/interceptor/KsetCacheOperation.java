package com.kset.cache.interceptor;

import com.kset.cache.core.KsetCacheLayer;

import java.util.List;

public final class KsetCacheOperation {

    public enum Kind {
        CACHEABLE,
        PUT,
        EVICT
    }

    private final Kind kind;
    private final String cacheName;
    private final String key;
    private final List<KsetCacheLayer> layers;
    private final String ttl;
    private final String nullTtl;
    private final boolean cacheNull;
    private final boolean beforeInvocation;

    KsetCacheOperation(Kind kind,
                       String cacheName,
                       String key,
                       List<KsetCacheLayer> layers,
                       String ttl,
                       String nullTtl,
                       boolean cacheNull,
                       boolean beforeInvocation) {
        this.kind = kind;
        this.cacheName = cacheName;
        this.key = key;
        this.layers = layers;
        this.ttl = ttl;
        this.nullTtl = nullTtl;
        this.cacheNull = cacheNull;
        this.beforeInvocation = beforeInvocation;
    }

    public Kind kind() {
        return kind;
    }

    String cacheName() {
        return cacheName;
    }

    String key() {
        return key;
    }

    public List<KsetCacheLayer> layers() {
        return layers;
    }

    String ttl() {
        return ttl;
    }

    String nullTtl() {
        return nullTtl;
    }

    boolean cacheNull() {
        return cacheNull;
    }

    boolean beforeInvocation() {
        return beforeInvocation;
    }
}
