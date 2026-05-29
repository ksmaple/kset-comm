package com.kset.cache.annotation;

import com.kset.cache.core.KsetCacheLayer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KsetCacheable {

    String cacheName();

    String key();

    KsetCacheLayer[] layers() default {KsetCacheLayer.L1, KsetCacheLayer.L2};

    String ttl() default "";

    String nullTtl() default "";

    boolean cacheNull() default true;
}
