package com.kset.demo.user.infrastructure.cache;

import com.kset.demo.user.domain.model.User;
import com.kset.demo.user.domain.repository.UserCacheRepository;
import com.kset.redis.core.KsetRedisService;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisUserCacheRepository implements UserCacheRepository {

    private final KsetRedisService redisService;

    public RedisUserCacheRepository(KsetRedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public User findById(Long id) {
        return redisService.get(cacheKey(id), User.class);
    }

    @Override
    public void save(User user, Duration ttl) {
        if (user.getId() != null) {
            redisService.setEx(cacheKey(user.getId()), user, ttl);
        }
    }

    private String cacheKey(Long id) {
        return "micro:user:" + id;
    }
}
