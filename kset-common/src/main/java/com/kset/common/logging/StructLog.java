package com.kset.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 与 {@link Logger} 绑定的结构化日志入口，避免每次调用都传入 {@code log}。
 *
 * <p>推荐在类内声明一次，与 {@code @Slf4j} 的 {@code log} 并存时共用同一 Logger 名：</p>
 *
 * <pre>{@code
 * @Slf4j
 * public class OrderService {
 *     private static final StructLog LOG = StructLog.of(OrderService.class);
 *
 *     public void create(Order order) {
 *         LOG.info("order created", "orderId", order.getId());
 *         log.debug("plain debug: {}", order);
 *     }
 * }
 * }</pre>
 */
public final class StructLog {

    private final Logger logger;

    private StructLog(Logger logger) {
        this.logger = logger;
    }

    public static StructLog of(Class<?> type) {
        return new StructLog(LoggerFactory.getLogger(type));
    }

    public static StructLog of(Logger logger) {
        return new StructLog(logger);
    }

    public void trace(String message, Object... kvs) {
        LogUtil.trace(logger, message, kvs);
    }

    public void debug(String message, Object... kvs) {
        LogUtil.debug(logger, message, kvs);
    }

    public void info(String message, Object... kvs) {
        LogUtil.info(logger, message, kvs);
    }

    public void warn(String message, Object... kvs) {
        LogUtil.warn(logger, message, kvs);
    }

    public void error(String message, Object... kvs) {
        LogUtil.error(logger, message, kvs);
    }

    public void error(String message, Throwable t, Object... kvs) {
        LogUtil.error(logger, message, t, kvs);
    }
}
