package com.kset.redis.lock;

/**
 * ???????????
 */
public class KsetRedisLockBusyException extends KsetRedisLockException {

    public KsetRedisLockBusyException(String lockKey) {
        super(lockKey, "Lock is busy, rejected immediately: " + lockKey);
    }
}
