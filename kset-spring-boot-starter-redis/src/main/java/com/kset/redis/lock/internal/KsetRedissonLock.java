package com.kset.redis.lock.internal;

import com.kset.redis.lock.KsetRedisLock;
import org.redisson.api.RLock;

import java.util.List;
import java.util.Objects;

class KsetRedissonLock implements KsetRedisLock {

    private final String lockKey;
    private final List<String> lockKeys;
    private final RLock lock;

    KsetRedissonLock(String lockKey, RLock lock) {
        this(lockKey, List.of(lockKey), lock);
    }

    KsetRedissonLock(String compositeKey, List<String> lockKeys, RLock lock) {
        this.lockKey = Objects.requireNonNull(compositeKey, "lockKey");
        this.lockKeys = List.copyOf(lockKeys);
        this.lock = Objects.requireNonNull(lock, "lock");
    }

    @Override
    public String lockKey() {
        return lockKey;
    }

    @Override
    public List<String> lockKeys() {
        return lockKeys;
    }

    @Override
    public boolean isMultiLock() {
        return lockKeys.size() > 1;
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }

    @Override
    public void unlock() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    RLock redissonLock() {
        return lock;
    }
}
