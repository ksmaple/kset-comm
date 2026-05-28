package com.kset.redis.support;

import com.kset.redis.core.KsetRedisRegistry;
import com.kset.redis.core.KsetRedisService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 命名 Redis 数据源集合（由多源自动配置产出）。
 */
public class KsetRedisNamedSources {

    private final Map<String, KsetRedisService> services;

    public KsetRedisNamedSources(Map<String, KsetRedisService> services) {
        this.services = services != null ? Map.copyOf(services) : Map.of();
    }

    public static KsetRedisNamedSources empty() {
        return new KsetRedisNamedSources(Collections.emptyMap());
    }

    public Map<String, KsetRedisService> getServices() {
        return services;
    }

    public void registerAll(KsetRedisRegistry registry) {
        services.forEach(registry::register);
    }
}
