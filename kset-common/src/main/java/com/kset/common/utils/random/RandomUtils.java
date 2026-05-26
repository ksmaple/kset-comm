package com.kset.common.utils.random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数工具（基于 {@link ThreadLocalRandom}）。
 */
public final class RandomUtils {

    private RandomUtils() {
    }

    /**
     * 闭区间 [start, end] 内的随机整数。
     */
    public static int nextIntInclusive(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("start 不能大于 end: " + start + " > " + end);
        }
        return ThreadLocalRandom.current().nextInt(start, end + 1);
    }

    /**
     * 兼容旧 API 命名。
     */
    public static int numberRandom(int start, int end) {
        return nextIntInclusive(start, end);
    }

    /**
     * 左闭右开 [0, bound) 的随机整数。
     */
    public static int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound 必须大于 0: " + bound);
        }
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static long nextLong(long bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound 必须大于 0: " + bound);
        }
        return ThreadLocalRandom.current().nextLong(bound);
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * 左闭右开 [origin, bound) 的随机 double。
     */
    public static double nextDouble(double origin, double bound) {
        return ThreadLocalRandom.current().nextDouble(origin, bound);
    }
}
