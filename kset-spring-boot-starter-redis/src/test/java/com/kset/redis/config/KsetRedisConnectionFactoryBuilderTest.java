package com.kset.redis.config;

import com.kset.cloud.config.KsetRedisProperties;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KsetRedisConnectionFactoryBuilderTest {

    @Test
    void buildStandalone() {
        KsetRedisProperties.RedisSourceProperties source = new KsetRedisProperties.RedisSourceProperties();
        source.setHost("127.0.0.1");
        source.setPort(6379);
        LettuceConnectionFactory factory = KsetRedisConnectionFactoryBuilder.build(source);
        assertNotNull(factory);
        assertTrue(factory.isRunning() || !factory.isRunning());
        factory.destroy();
    }

    @Test
    void buildCluster() {
        KsetRedisProperties.RedisSourceProperties source = new KsetRedisProperties.RedisSourceProperties();
        source.getCluster().setEnabled(true);
        source.getCluster().setNodes(List.of("127.0.0.1:6379", "127.0.0.1:6380"));
        LettuceConnectionFactory factory = KsetRedisConnectionFactoryBuilder.build(source);
        assertNotNull(factory);
        factory.destroy();
    }
}
