package com.kset.common.logging;

import org.slf4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

/**
 * 结构化日志底层工具（logstash {@link net.logstash.logback.argument.StructuredArguments}）。
 *
 * <p><b>业务代码优先使用 {@link StructLog}</b>，在类内绑定一次 Logger：</p>
 *
 * <pre>{@code
 * private static final StructLog LOG = StructLog.of(OrderService.class);
 * LOG.info("order created", "orderId", order.getId());
 * }</pre>
 *
 * <p>本类静态方法适用于无法持有 {@link StructLog} 字段的场景（如一次性工具方法、
 * 已持有外部传入 {@link Logger} 的库代码）。</p>
 *
 * <p>对于纯文本 Appender（dev/test 控制台），参数以 {@code key=value} 形式附加在 message 之后，
 * 保证可读性不受损失。</p>
 */
public final class LogUtil {

    private LogUtil() {
    }

    public static void trace(Logger log, String message, Object... kvs) {
        if (log.isTraceEnabled()) {
            log.trace(message, toArgs(kvs));
        }
    }

    public static void debug(Logger log, String message, Object... kvs) {
        if (log.isDebugEnabled()) {
            log.debug(message, toArgs(kvs));
        }
    }

    public static void info(Logger log, String message, Object... kvs) {
        if (log.isInfoEnabled()) {
            log.info(message, toArgs(kvs));
        }
    }

    public static void warn(Logger log, String message, Object... kvs) {
        if (log.isWarnEnabled()) {
            log.warn(message, toArgs(kvs));
        }
    }

    public static void error(Logger log, String message, Object... kvs) {
        if (log.isErrorEnabled()) {
            log.error(message, toArgs(kvs));
        }
    }

    public static void error(Logger log, String message, Throwable t, Object... kvs) {
        if (log.isErrorEnabled()) {
            Object[] structuredArgs = toArgs(kvs);
            Object[] args = new Object[structuredArgs.length + 1];
            System.arraycopy(structuredArgs, 0, args, 0, structuredArgs.length);
            args[structuredArgs.length] = t;
            log.error(message, args);
        }
    }

    static Object[] toArgs(Object... kvs) {
        if (kvs == null || kvs.length == 0) {
            return new Object[0];
        }
        int pairCount = kvs.length / 2;
        Object[] args = new Object[pairCount];
        for (int i = 0; i < pairCount; i++) {
            String key = String.valueOf(kvs[i * 2]);
            Object value = kvs[i * 2 + 1];
            args[i] = keyValue(key, value);
        }
        return args;
    }
}
