package com.kset.redis.lock.internal;

import com.kset.redis.lock.KsetRedisLockScope;

final class KsetRedissonLockScope extends KsetRedissonLock implements KsetRedisLockScope {

    KsetRedissonLockScope(String compositeKey, java.util.List<String> lockKeys, org.redisson.api.RLock lock) {
        super(compositeKey, lockKeys, lock);
    }
}
