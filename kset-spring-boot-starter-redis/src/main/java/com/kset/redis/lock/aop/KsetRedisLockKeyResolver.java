package com.kset.redis.lock.aop;

import com.kset.redis.lock.annotation.KsetLocked;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析 {@link KsetLocked} 上的 SpEL 锁 key。
 */
public final class KsetRedisLockKeyResolver {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    public List<String> resolve(ProceedingJoinPoint joinPoint, KsetLocked locked) {
        if (locked.keys().length > 0) {
            List<String> keys = new ArrayList<>(locked.keys().length);
            for (String expression : locked.keys()) {
                keys.add(evaluate(joinPoint, expression));
            }
            return List.copyOf(keys);
        }
        if (!StringUtils.hasText(locked.value())) {
            throw new IllegalArgumentException("@KsetLocked requires non-empty value or keys");
        }
        return List.of(evaluate(joinPoint, locked.value()));
    }

    private static String evaluate(ProceedingJoinPoint joinPoint, String expression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = NAME_DISCOVERER.getParameterNames(method);
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        Expression expr = PARSER.parseExpression(expression);
        Object value = expr.getValue(context);
        if (value == null || !StringUtils.hasText(value.toString())) {
            throw new IllegalArgumentException("Lock key SpEL resolved to blank: " + expression);
        }
        return value.toString();
    }
}
