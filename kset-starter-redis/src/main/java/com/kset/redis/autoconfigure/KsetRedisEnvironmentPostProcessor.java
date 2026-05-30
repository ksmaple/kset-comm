package com.kset.redis.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 补全 Redis 业务约定默认值，避免业务 YAML 重复声明 KSet 私有键。
 */
public class KsetRedisEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "ksetRedisDefaults";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String appName = environment.getProperty("spring.application.name");
        if (appName == null || appName.isBlank() || environment.containsProperty("kset.redis.key-prefix")) {
            return;
        }

        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("kset.redis.key-prefix", "kset:" + appName.trim() + ":");
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, defaults));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
