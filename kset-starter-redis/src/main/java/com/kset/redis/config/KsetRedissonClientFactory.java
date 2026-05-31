package com.kset.redis.config;

import com.kset.cloud.config.KsetRedisProperties;
import com.kset.redis.codec.KsetFastjsonRedisSerializer;
import com.kset.redis.codec.KsetFastjsonRedissonCodec;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 基于 Spring {@link RedisProperties} 或 KSet 多源配置创建 {@link RedissonClient}。
 */
public final class KsetRedissonClientFactory {

    private KsetRedissonClientFactory() {
    }

    public static RedissonClient createPrimary(RedisProperties springRedis,
                                               KsetRedisProperties ksetRedis,
                                               KsetFastjsonRedisSerializer valueSerializer) {
        Config config = baseConfig(ksetRedis, valueSerializer);
        if (springRedis.getCluster() != null && springRedis.getCluster().getNodes() != null
                && !springRedis.getCluster().getNodes().isEmpty()) {
            ClusterServersConfig cluster = config.useClusterServers();
            cluster.addNodeAddress(toAddresses(springRedis.getCluster().getNodes()));
            applyPassword(cluster, springRedis.getPassword());
        } else {
            SingleServerConfig single = config.useSingleServer();
            String host = springRedis.getHost() != null ? springRedis.getHost() : "localhost";
            int port = springRedis.getPort();
            single.setAddress("redis://" + host + ":" + port);
            single.setDatabase(springRedis.getDatabase());
            applyPassword(single, springRedis.getPassword());
        }
        return Redisson.create(config);
    }

    public static RedissonClient createFromSource(
            KsetRedisProperties.RedisSourceProperties source,
            KsetRedisProperties ksetRedis,
            KsetFastjsonRedisSerializer valueSerializer) {
        Config config = baseConfig(ksetRedis, valueSerializer);
        if (source.isClusterMode()) {
            ClusterServersConfig cluster = config.useClusterServers();
            cluster.addNodeAddress(toAddresses(source.getCluster().getNodes()));
            applyPassword(cluster, source.getPassword());
        } else {
            SingleServerConfig single = config.useSingleServer();
            single.setAddress("redis://" + source.getHost() + ":" + source.getPort());
            single.setDatabase(source.getDatabase());
            applyPassword(single, source.getPassword());
        }
        return Redisson.create(config);
    }

    private static Config baseConfig(KsetRedisProperties ksetRedis, KsetFastjsonRedisSerializer valueSerializer) {
        Config config = new Config();
        config.setCodec(new KsetFastjsonRedissonCodec(valueSerializer));
        KsetRedisProperties.Redisson redisson = ksetRedis.getRedisson();
        config.setThreads(redisson.getThreads());
        config.setNettyThreads(redisson.getNettyThreads());
        return config;
    }

    private static String[] toAddresses(List<String> nodes) {
        return nodes.stream()
                .filter(StringUtils::hasText)
                .map(KsetRedissonClientFactory::normalizeAddress)
                .toArray(String[]::new);
    }

    private static String normalizeAddress(String node) {
        if (node.startsWith("redis://") || node.startsWith("rediss://")) {
            return node;
        }
        return "redis://" + node;
    }

    private static void applyPassword(SingleServerConfig config, String password) {
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }

    private static void applyPassword(ClusterServersConfig config, String password) {
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }
}
