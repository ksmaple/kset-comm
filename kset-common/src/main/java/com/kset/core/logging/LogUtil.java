package com.kset.core.logging;

import org.slf4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

/**
 * 结构化日志工具类。
 *
 * <p>对 {@link net.logstash.logback.argument.StructuredArguments#keyValue}
 * 做薄封装，简化调用方代码：
 *
 * <pre>{@code
 * LogUtil.info(log, "Document uploaded",
 *     "documentId", docId,
 *     "folderId", folderId,
 *     "sizeBytes", size);
 * }</pre>
 *
 * <p>输出 JSON 示例：
 * <pre>{@code
 * {
 *   "message": "Document uploaded",
 *   "arguments": {
 *     "documentId": 123,
 *     "folderId": 456,
 *     "sizeBytes": 1024
 *   }
 * }
 * }</pre>
 *
 * <p>对于纯文本 Appender（dev/test 控制台），参数以 {@code key=value} 形式附加在 message 之后，
 * 保证可读性不受损失。
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
            Object[] args = new Object[kvs.length + 1];
            System.arraycopy(toArgs(kvs), 0, args, 0, kvs.length);
            args[kvs.length] = t;
            log.error(message, args);
        }
    }

    private static Object[] toArgs(Object... kvs) {
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
