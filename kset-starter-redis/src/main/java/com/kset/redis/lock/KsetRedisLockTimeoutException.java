package com.kset.redis.lock;

import java.time.Duration;

/**
 * ??????????
 */
public class KsetRedisLockTimeoutException extends KsetRedisLockException {

    private final Duration waitTime;

    public KsetRedisLockTimeoutException(String lockKey, Duration waitTime) {
        super(lockKey, "Failed to acquire lock within " + waitTime + ": " + lockKey);
        this.waitTime = waitTime;
    }

    public Duration waitTime() {
        return waitTime;
    }
}
