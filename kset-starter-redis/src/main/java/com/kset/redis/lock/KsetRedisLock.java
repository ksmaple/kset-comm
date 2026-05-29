package com.kset.redis.lock;

import java.util.List;

/**
 * Acquired Redis lock handle. Call {@link #unlock()} or use try-with-resources to release it.
 */
public interface KsetRedisLock extends AutoCloseable {

    /**
     * Composite lock key, such as {@code a|b|c} for multi-locks.
     */
    String lockKey();

    /**
     * Original lock keys used to acquire this lock.
     */
    List<String> lockKeys();

    default boolean isMultiLock() {
        return lockKeys().size() > 1;
    }

    boolean isHeldByCurrentThread();

    void unlock();

    @Override
    default void close() {
        unlock();
    }
}
