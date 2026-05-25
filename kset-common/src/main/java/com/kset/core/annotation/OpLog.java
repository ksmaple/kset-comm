package com.kset.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 *
 * <p>标记在 Controller 方法上，AOP 切面会自动记录操作日志。
 *
 * <p>示例：
 * <pre>{@code
 * @OpLog(type = "CREATE", target = "document")
 * public ApiResult<IdResponse> create(@RequestBody CreateDocumentCommand command) { ... }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpLog {

    /** 操作类型，如 CREATE / UPDATE / DELETE / LOGIN / LOGOUT / EXPORT / IMPORT */
    String type();

    /** 目标对象类型，如 document / user / role / permission */
    String target() default "";

    /** 目标对象ID SpEL 表达式，如 "#command.id" 或 "#request.id" */
    String targetId() default "";

    /** 目标对象名称 SpEL 表达式 */
    String targetName() default "";

    /** 是否记录请求参数 */
    boolean recordParams() default false;

    /** 是否记录响应结果 */
    boolean recordResult() default false;
}
