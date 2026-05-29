package com.kset.common.logging;

import org.slf4j.MDC;

/**
 * 操作日志上下文（MDC {@code operator} 键）。
 *
 * <p>写入 MDC 以便 JSON 日志与 logback pattern 自动携带操作人；
 * 跨线程传播需配合 {@link com.kset.common.monitor.Monitor#openScope}。
 */
public final class OpLogContext {

    static final String OPERATOR_KEY = "operator";

    private OpLogContext() {
    }

    public static void setOperator(String operator) {
        if (operator != null && !operator.isBlank()) {
            MDC.put(OPERATOR_KEY, operator);
        }
    }

    public static String getOperator() {
        return MDC.get(OPERATOR_KEY);
    }

    public static void clear() {
        MDC.remove(OPERATOR_KEY);
    }
}
