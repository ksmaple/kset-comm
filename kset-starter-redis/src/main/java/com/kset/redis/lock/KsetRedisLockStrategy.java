package com.kset.redis.lock;

/**
 * Strategy used when a Redis lock is already held.
 */
public enum KsetRedisLockStrategy {

    /**
     * Fail immediately when the lock is busy.
     */
    REJECT_IF_BUSY,

    /**
     * Wait for the configured timeout, then fail.
     */
    WAIT_THEN_FAIL,

    /**
     * Return empty result when the lock is busy.
     */
    OPTIONAL,

    /**
     * Block until the lock is acquired.
     */
    BLOCK_UNTIL_ACQUIRED
}
