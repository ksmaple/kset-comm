package com.kset.redis.lock;

/**
 * Thrown when a lock cannot be acquired immediately.
 */
public class KsetRedisLockBusyException extends KsetRedisLockException {

    public KsetRedisLockBusyException(String lockKey) {
        super(lockKey, "Lock is busy, rejected immediately: " + lockKey);
    }
}
