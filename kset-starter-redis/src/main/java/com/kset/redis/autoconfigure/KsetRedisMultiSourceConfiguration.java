package com.kset.redis.autoconfigure;

import com.kset.cloud.config.KsetRedisProperties;
import com.kset.redis.config.KsetRedisConnectionFactoryBuilder;
import com.kset.redis.config.KsetRedisTemplateFactory;
import com.kset.redis.core.KsetRedisRegistry;
import com.kset.redis.core.KsetRedisService;
import com.kset.redis.core.KsetRedisStreamSettings;
import com.kset.redis.core.KsetRedisTtlPolicy;
import com.kset.redis.support.KsetRedisNamedSources;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 声明式多 Redis 数据源（{@code kset.redis.sources.*}）。
 */
@Configuration
@EnableConfigurationProperties(KsetRedisProperties.class)
@ConditionalOnProperty(prefix = "kset.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KsetRedisMultiSourceConfiguration {

    public static String namedServiceBeanName(String sourceName) {
        return sourceName + "KsetRedisService";
    }

    @Bean
    public KsetRedisNamedSources ksetRedisNamedSources(KsetRedisProperties properties,
                                                       KsetRedisTtlPolicy ttlPolicy,
                                                       KsetRedisStreamSettings streamSettings,
                                                       ConfigurableListableBeanFactory beanFactory) {
        Map<String, KsetRedisProperties.RedisSourceProperties> sources = properties.getSources();
        if (sources == null || sources.isEmpty()) {
            return KsetRedisNamedSources.empty();
        }
        Map<String, KsetRedisService> services = new LinkedHashMap<>();
        for (Map.Entry<String, KsetRedisProperties.RedisSourceProperties> entry : sources.entrySet()) {
            String name = entry.getKey();
            KsetRedisProperties.RedisSourceProperties source = entry.getValue();
            if (!StringUtils.hasText(name)) {
                continue;
            }
            if (KsetRedisRegistry.PRIMARY_NAME.equals(name)) {
                throw new IllegalStateException("Reserved redis source name: " + KsetRedisRegistry.PRIMARY_NAME);
            }
            if (source == null || !source.isEnabled()) {
                continue;
            }
            LettuceConnectionFactory connectionFactory = KsetRedisConnectionFactoryBuilder.build(source);
            RedisTemplate<String, Object> template =
                    KsetRedisTemplateFactory.create(connectionFactory, source.getKeyPrefix());
            KsetRedisService service = KsetRedisService.from(name, template, ttlPolicy, streamSettings);
            services.put(name, service);
            beanFactory.registerSingleton(namedServiceBeanName(name), service);
        }
        return new KsetRedisNamedSources(services);
    }
}
