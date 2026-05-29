package com.kset.cache.core;

import java.time.Duration;
import java.util.Optional;

public interface KsetCacheStore {

    KsetCacheLayer layer();

    Optional<KsetCacheValue> get(KsetCacheSpec spec);

    void put(KsetCacheSpec spec, KsetCacheValue value, Duration ttl);

    void evict(KsetCacheSpec spec);
}
