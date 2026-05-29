package com.kset.cache.core;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public final class KsetCacheSpec {

    private final String cacheName;
    private final String key;
    private final List<KsetCacheLayer> layers;
    private final Duration ttl;
    private final Duration nullTtl;
    private final boolean cacheNull;
    private final Class<?> valueType;

    public static Builder builder(String cacheName, String key) {
        return new Builder(cacheName, key);
    }

    public KsetCacheSpec(String cacheName,
                         String key,
                         List<KsetCacheLayer> layers,
                         Duration ttl,
                         Duration nullTtl,
                         boolean cacheNull,
                         Class<?> valueType) {
        this.cacheName = requireText(cacheName, "cacheName");
        this.key = requireText(key, "key");
        this.layers = List.copyOf(Objects.requireNonNull(layers, "layers"));
        this.ttl = ttl;
        this.nullTtl = nullTtl;
        this.cacheNull = cacheNull;
        this.valueType = valueType != null ? valueType : Object.class;
        if (this.layers.isEmpty()) {
            throw new IllegalArgumentException("layers must not be empty");
        }
    }

    public String cacheName() {
        return cacheName;
    }

    public String key() {
        return key;
    }

    public List<KsetCacheLayer> layers() {
        return layers;
    }

    public Duration ttl() {
        return ttl;
    }

    public Duration nullTtl() {
        return nullTtl;
    }

    public boolean cacheNull() {
        return cacheNull;
    }

    public Class<?> valueType() {
        return valueType;
    }

    public String fullKey() {
        return cacheName + "::" + key;
    }

    public Duration ttlFor(Object value) {
        if (value == null) {
            return nullTtl != null ? nullTtl : ttl;
        }
        return ttl;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    public static final class Builder {

        private final String cacheName;
        private final String key;
        private List<KsetCacheLayer> layers = List.of(KsetCacheLayer.L1);
        private Duration ttl;
        private Duration nullTtl;
        private boolean cacheNull = true;
        private Class<?> valueType = Object.class;

        private Builder(String cacheName, String key) {
            this.cacheName = cacheName;
            this.key = key;
        }

        public Builder layers(KsetCacheLayer... layers) {
            this.layers = List.of(layers);
            return this;
        }

        public Builder layers(List<KsetCacheLayer> layers) {
            this.layers = layers;
            return this;
        }

        public Builder ttl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder nullTtl(Duration nullTtl) {
            this.nullTtl = nullTtl;
            return this;
        }

        public Builder cacheNull(boolean cacheNull) {
            this.cacheNull = cacheNull;
            return this;
        }

        public Builder valueType(Class<?> valueType) {
            this.valueType = valueType;
            return this;
        }

        public KsetCacheSpec build() {
            return new KsetCacheSpec(cacheName, key, layers, ttl, nullTtl, cacheNull, valueType);
        }
    }
}
