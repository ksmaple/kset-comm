package com.kset.redis.monitor;

import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.facade.MonitorStatus;
import com.kset.common.monitor.facade.MonitorTransaction;
import com.kset.common.monitor.facade.MonitorTypes;
import com.kset.redis.core.KsetRedisOperations;
import com.kset.redis.rank.KsetRedisRankBoard;
import com.kset.redis.rank.group.KsetRedisGroupRankBoard;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * KSet Redis 监控工具。仅包装 KSet Redis 门面对象，底层 RedisTemplate / RedissonClient 保持原生行为。
 */
public final class KsetRedisMonitor {

    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    private KsetRedisMonitor() {
    }

    public static KsetRedisOperations wrapOperations(KsetRedisOperations delegate) {
        Objects.requireNonNull(delegate, "delegate");
        return (KsetRedisOperations) Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(),
                new Class<?>[]{KsetRedisOperations.class},
                new OperationsInvocationHandler(delegate));
    }

    public static KsetRedisRankBoard wrapRankBoard(KsetRedisRankBoard delegate) {
        Objects.requireNonNull(delegate, "delegate");
        return (KsetRedisRankBoard) Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(),
                new Class<?>[]{KsetRedisRankBoard.class},
                new RankBoardInvocationHandler(delegate, "rank"));
    }

    public static KsetRedisGroupRankBoard wrapGroupRankBoard(KsetRedisGroupRankBoard delegate) {
        Objects.requireNonNull(delegate, "delegate");
        return (KsetRedisGroupRankBoard) Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(),
                new Class<?>[]{KsetRedisGroupRankBoard.class},
                new RankBoardInvocationHandler(delegate, "group-rank"));
    }

    public static void run(String source, String operation, Runnable action) {
        try {
            call(source, operation, () -> {
                action.run();
                return null;
            });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T call(String source, String operation, Callable<T> action) throws Exception {
        if (Boolean.TRUE.equals(ACTIVE.get())) {
            return action.call();
        }
        String txName = source + "." + operation;
        ACTIVE.set(true);
        try (MonitorTransaction tx = Monitor.newTransaction(MonitorTypes.CACHE, txName)) {
            tx.addData("component", "redis");
            tx.addData("source", source);
            tx.addData("operation", operation);
            try {
                T result = action.call();
                tx.setStatus(MonitorStatus.SUCCESS);
                return result;
            } catch (Exception e) {
                tx.setStatus(e);
                tx.addData("errorType", e.getClass().getSimpleName());
                Monitor.logError(e, txName);
                throw e;
            }
        } finally {
            ACTIVE.remove();
        }
    }

    private static final class OperationsInvocationHandler implements InvocationHandler {

        private final KsetRedisOperations delegate;

        private OperationsInvocationHandler(KsetRedisOperations delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class || isMetadataMethod(method)) {
                return method.invoke(delegate, args);
            }
            String source = delegate.sourceName();
            return invokeMonitored(source, method.getName(), () -> method.invoke(delegate, args));
        }
    }

    private static final class RankBoardInvocationHandler implements InvocationHandler {

        private final Object delegate;
        private final String source;

        private RankBoardInvocationHandler(Object delegate, String source) {
            this.delegate = delegate;
            this.source = source;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class || "boardId".equals(method.getName())
                    || "order".equals(method.getName())) {
                return method.invoke(delegate, args);
            }
            String boardId = String.valueOf(delegate.getClass().getMethod("boardId").invoke(delegate));
            return invokeMonitored(source, boardId + "." + method.getName(), () -> method.invoke(delegate, args));
        }
    }

    private static Object invokeMonitored(String source, String operation, ThrowingSupplier supplier) throws Throwable {
        try {
            return call(source, operation, () -> {
                try {
                    return supplier.get();
                } catch (InvocationTargetException e) {
                    Throwable target = e.getTargetException();
                    if (target instanceof Exception exception) {
                        throw exception;
                    }
                    throw e;
                }
            });
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private static boolean isMetadataMethod(Method method) {
        String name = method.getName();
        return "sourceName".equals(name) || "template".equals(name);
    }

    @FunctionalInterface
    private interface ThrowingSupplier {
        Object get() throws Exception;
    }
}
