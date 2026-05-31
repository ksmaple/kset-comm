package com.kset.mq.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface KsetMqEvent {

    /**
     * 事件发送 Topic；为空时使用 rocketmq.producer.topic 或应用名默认 Topic。
     */
    String topic() default "";

    /**
     * 事件发送 Tag；为空时不追加 Tag。
     */
    String tag() default "";
}
