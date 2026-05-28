package com.kset.redis.config;

import com.kset.cloud.config.KsetRedisProperties;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据 {@link KsetRedisProperties.RedisSourceProperties} 构建 Lettuce 连接工厂（单机/集群）。
 */
public final class KsetRedisConnectionFactoryBuilder {

    private KsetRedisConnectionFactoryBuilder() {
    }

    public static LettuceConnectionFactory build(KsetRedisProperties.RedisSourceProperties source) {
        LettuceClientConfiguration clientConfig = lettuceClientConfiguration(source);
        LettuceConnectionFactory factory;
        if (source.isClusterMode()) {
            factory = new LettuceConnectionFactory(clusterConfiguration(source), clientConfig);
        } else {
            factory = new LettuceConnectionFactory(standaloneConfiguration(source), clientConfig);
        }
        factory.afterPropertiesSet();
        return factory;
    }

    private static RedisStandaloneConfiguration standaloneConfiguration(
            KsetRedisProperties.RedisSourceProperties source) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(source.getHost(), source.getPort());
        config.setDatabase(source.getDatabase());
        if (StringUtils.hasText(source.getPassword())) {
            config.setPassword(RedisPassword.of(source.getPassword()));
        }
        return config;
    }

    private static RedisClusterConfiguration clusterConfiguration(
            KsetRedisProperties.RedisSourceProperties source) {
        KsetRedisProperties.Cluster cluster = source.getCluster();
        List<String> nodes = cluster.getNodes().stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
        RedisClusterConfiguration config = new RedisClusterConfiguration(nodes);
        config.setMaxRedirects(cluster.getMaxRedirects());
        if (StringUtils.hasText(source.getPassword())) {
            config.setPassword(RedisPassword.of(source.getPassword()));
        }
        return config;
    }

    private static LettuceClientConfiguration lettuceClientConfiguration(
            KsetRedisProperties.RedisSourceProperties source) {
        Duration timeout = source.getTimeout() != null ? source.getTimeout() : Duration.ofSeconds(2);
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
                LettuceClientConfiguration.builder()
                        .commandTimeout(timeout);
        if (source.isSsl()) {
            builder.useSsl();
        }
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(timeout)
                .build();
        builder.clientOptions(ClientOptions.builder().socketOptions(socketOptions).build());
        return builder.build();
    }
}
