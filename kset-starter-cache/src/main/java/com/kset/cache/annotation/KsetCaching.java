package com.kset.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KsetCaching {

    KsetCacheable[] cacheable() default {};

    KsetCachePut[] put() default {};

    KsetCacheEvict[] evict() default {};
}
