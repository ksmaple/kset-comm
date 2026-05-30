package com.kset.cache.annotation;

import com.kset.cache.core.KsetCacheLayer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 方法结果读取缓存；未命中时执行目标方法并按配置写入缓存。
 */
public @interface KsetCacheable {

    /**
     * 缓存名称，用于区分业务缓存空间。
     */
    String cacheName();

    /**
     * 缓存 key，支持 SpEL 表达式。
     */
    String key();

    /**
     * 参与读写的缓存层级，默认先查 L1 再查 L2。
     */
    KsetCacheLayer[] layers() default {KsetCacheLayer.L1, KsetCacheLayer.L2};

    /**
     * 非空结果的过期时间；空值时使用全局或层级默认 TTL。
     */
    String ttl() default "";

    /**
     * 空值结果的过期时间，通常短于正常结果 TTL。
     */
    String nullTtl() default "";

    /**
     * 是否缓存 null 结果，用于防止缓存穿透。
     */
    boolean cacheNull() default true;
}
