package com.kset.cache.interceptor;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

public class KsetCacheKeyEvaluator {

    private static final Object NO_RESULT = new Object();

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public String evalKey(String expression, Method method, Object[] args, Object target, Object result) {
        Object value = parser.parseExpression(expression)
                .getValue(context(method, args, target, result));
        if (value == null) {
            throw new IllegalArgumentException("cache key expression returned null: " + expression);
        }
        return String.valueOf(value);
    }

    private MethodBasedEvaluationContext context(Method method, Object[] args, Object target, Object result) {
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                new KsetCacheExpressionRoot(target, method),
                method,
                args != null ? args : new Object[0],
                parameterNameDiscoverer);
        if (result != NO_RESULT) {
            context.setVariable("result", result);
        }
        return context;
    }

    public static Object noResult() {
        return NO_RESULT;
    }
}
