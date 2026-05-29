package com.kset.redis.autoconfigure;

import com.kset.cache.core.KsetCacheStore;
import com.kset.redis.cache.RedisKsetCacheStore;
import com.kset.redis.core.KsetRedisService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = KsetRedisAutoConfiguration.class)
@ConditionalOnClass(name = "com.kset.cache.core.KsetCacheStore")
@ConditionalOnBean(KsetRedisService.class)
public class KsetRedisCacheStoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "redisKsetCacheStore")
    public KsetCacheStore redisKsetCacheStore(KsetRedisService redisService) {
        return new RedisKsetCacheStore(redisService);
    }
}
