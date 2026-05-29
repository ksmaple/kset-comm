/**
 * KSet 日志工具包：结构化输出、脱敏、业务流程步骤（{@link com.kset.common.logging.FlowLogContext}）、操作人上下文。
 *
 * <p><b>分布式链路上下文（traceId / spanId / grayTag）由 {@link com.kset.common.monitor.Monitor} 写入 MDC，
 * 本包只消费，不自行存储或传播。</b> logback 通过 {@code %X{traceId}} 等自动输出；
 * 业务代码使用 {@link com.kset.common.logging.StructLog} 时 traceId 会随 MDC 进入 JSON 日志。
 *
 * <p>两类 trace 职责边界：
 * <ul>
 *   <li>请求级链路 ID → {@code com.kset.common.monitor}（HTTP/Dubbo/Gateway/线程池传播）</li>
 *   <li>业务流程步骤 → {@code FlowLogContext}（{@code flow.*} MDC 键，与 traceId 共存）</li>
 * </ul>
 */
package com.kset.common.logging;
