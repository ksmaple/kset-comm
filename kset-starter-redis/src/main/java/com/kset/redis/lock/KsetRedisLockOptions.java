package com.kset.redis.lock;

import java.time.Duration;
import java.util.Objects;

/**
 * Lock acquisition options.
 */
public final class KsetRedisLockOptions {

    private final Duration waitTime;
    private final Duration leaseTime;
    private final KsetRedisLockStrategy strategy;

    private KsetRedisLockOptions(Duration waitTime, Duration leaseTime, KsetRedisLockStrategy strategy) {
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static KsetRedisLockOptions rejectNow(Duration leaseTime) {
        return builder().waitTime(Duration.ZERO).leaseTime(leaseTime).strategy(KsetRedisLockStrategy.REJECT_IF_BUSY).build();
    }

    public static KsetRedisLockOptions waitThenFail(Duration waitTime, Duration leaseTime) {
        return builder().waitTime(waitTime).leaseTime(leaseTime).strategy(KsetRedisLockStrategy.WAIT_THEN_FAIL).build();
    }

    public static KsetRedisLockOptions blockUntil(Duration leaseTime) {
        return builder().strategy(KsetRedisLockStrategy.BLOCK_UNTIL_ACQUIRED).leaseTime(leaseTime).build();
    }

    public Duration waitTime() {
        return waitTime;
    }

    public Duration leaseTime() {
        return leaseTime;
    }

    public KsetRedisLockStrategy strategy() {
        return strategy;
    }

    public static final class Builder {

        private Duration waitTime;
        private Duration leaseTime;
        private KsetRedisLockStrategy strategy = KsetRedisLockStrategy.REJECT_IF_BUSY;

        public Builder waitTime(Duration waitTime) {
            this.waitTime = waitTime;
            return this;
        }

        public Builder leaseTime(Duration leaseTime) {
            this.leaseTime = leaseTime;
            return this;
        }

        public Builder strategy(KsetRedisLockStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public KsetRedisLockOptions build() {
            return new KsetRedisLockOptions(waitTime, leaseTime, strategy);
        }
    }
}
