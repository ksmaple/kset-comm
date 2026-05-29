package com.kset.redis.lock;

/**
 * Base Redis lock runtime exception.
 */
public class KsetRedisLockException extends RuntimeException {

    private final String lockKey;

    public KsetRedisLockException(String lockKey, String message) {
        super(message);
        this.lockKey = lockKey;
    }

    public KsetRedisLockException(String lockKey, String message, Throwable cause) {
        super(message, cause);
        this.lockKey = lockKey;
    }

    public String lockKey() {
        return lockKey;
    }
}
