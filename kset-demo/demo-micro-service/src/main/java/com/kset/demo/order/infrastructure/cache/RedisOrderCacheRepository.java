package com.kset.demo.order.infrastructure.cache;

import com.kset.demo.order.domain.model.OrderView;
import com.kset.demo.order.domain.repository.OrderCacheRepository;
import com.kset.redis.core.KsetRedisService;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisOrderCacheRepository implements OrderCacheRepository {

    private final KsetRedisService redisService;

    public RedisOrderCacheRepository(KsetRedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public OrderView findViewById(Long id) {
        return redisService.get(cacheKey(id), OrderView.class);
    }

    @Override
    public void saveView(Long id, OrderView view, Duration ttl) {
        redisService.setEx(cacheKey(id), view, ttl);
    }

    private String cacheKey(Long id) {
        return "micro:order:" + id;
    }
}
