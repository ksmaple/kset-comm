package com.kset.cache.core;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public interface KsetCacheFacade {

    Optional<KsetCacheValue> get(KsetCacheSpec spec);

    void put(KsetCacheSpec spec, Object value);

    void evict(KsetCacheSpec spec);

    Object getOrLoad(List<KsetCacheSpec> specs, Callable<Object> loader) throws Exception;

    <T> Optional<T> getValue(KsetCacheSpec spec, Class<T> type);

    <T> T getOrLoadValue(KsetCacheSpec spec, Class<T> type, Callable<T> loader) throws Exception;

    KsetCacheMetrics metrics();
}
