package com.kset.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 组合多个缓存操作，适合单个方法同时读写或清理多个缓存 key。
 */
public @interface KsetCaching {

    /**
     * 读缓存操作集合。
     */
    KsetCacheable[] cacheable() default {};

    /**
     * 写缓存操作集合。
     */
    KsetCachePut[] put() default {};

    /**
     * 清理缓存操作集合。
     */
    KsetCacheEvict[] evict() default {};
}
