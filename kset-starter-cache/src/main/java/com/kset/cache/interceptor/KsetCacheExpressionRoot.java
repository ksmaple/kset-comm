package com.kset.cache.interceptor;

import java.lang.reflect.Method;

public final class KsetCacheExpressionRoot {

    private final Object target;
    private final Method method;

    public KsetCacheExpressionRoot(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return target != null ? target.getClass() : null;
    }

    public Method getMethod() {
        return method;
    }

    public String getMethodName() {
        return method.getName();
    }
}
