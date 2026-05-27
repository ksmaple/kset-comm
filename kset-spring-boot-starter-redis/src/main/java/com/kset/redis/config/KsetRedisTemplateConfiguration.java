package com.kset.redis.config;

import com.kset.cloud.config.KsetRedisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class KsetRedisTemplateConfiguration {

    @Bean(name = {"ksetRedisTemplate", "redisTemplate"})
    @Primary
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> ksetRedisTemplate(RedisConnectionFactory connectionFactory,
                                                           KsetRedisProperties properties) {
        return KsetRedisTemplateFactory.create(connectionFactory, properties.getKeyPrefix());
    }
}
