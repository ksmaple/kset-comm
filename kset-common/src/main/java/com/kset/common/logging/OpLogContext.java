package com.kset.common.logging;

import org.slf4j.MDC;


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
