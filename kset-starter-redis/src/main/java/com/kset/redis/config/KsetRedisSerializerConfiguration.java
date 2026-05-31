package com.kset.redis.config;

import com.kset.redis.codec.KsetFastjsonRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class KsetRedisSerializerConfiguration {

    public static final String BEAN_NAME = "ksetRedisValueSerializer";

    @Bean(BEAN_NAME)
    @ConditionalOnMissingBean(name = BEAN_NAME)
    public KsetFastjsonRedisSerializer ksetRedisValueSerializer() {
        return new KsetFastjsonRedisSerializer();
    }
}
