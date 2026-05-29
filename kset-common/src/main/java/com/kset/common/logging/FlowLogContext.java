package com.kset.common.logging;

import com.kset.common.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * 流程一致性日志上下文工具。
 *
 * <p>为关键业务流程注入 {@code flow.*} 系列 MDC 字段，支持日志驱动的
 * 流程完整性校验（断点检测、步骤连续性、事件链完整性）。
 *
 * <p><b>与 {@link Monitor} 协作：</b>
 * <ul>
 *   <li>分布式 traceId 由 Monitor 管理（通常由 Servlet/Gateway Filter 入站绑定），本类不写入 traceId</li>
 *   <li>{@link #clear()} 仅移除 {@code flow.*} 键，不调用 {@link Monitor#clear()}</li>
 *   <li>跨线程执行 flow 步骤时，用 {@link Monitor#openScope(Monitor#capture())} 恢复链路上下文</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * String flowId = FlowLogContext.beginFlow("document_upload", "user_123");
 * try {
 *     FlowLogContext.step("validate_file", FlowEventType.ENTER);
 *     validate(file);
 *     FlowLogContext.step("validate_file", FlowEventType.EXIT);
 *
 *     FlowLogContext.step("save_document", FlowEventType.ENTER);
 *     Document doc = save(file);
 *     FlowLogContext.step("save_document", FlowEventType.EXIT);
 *
 *     FlowLogContext.endFlow();
 * } catch (Exception e) {
 *     FlowLogContext.error("save_document", e);
 *     throw e;
 * } finally {
 *     FlowLogContext.clear();
 * }
 *
 * // 跨线程：同时传播 Monitor 链路与 flow MDC
 * TraceSnapshot snapshot = Monitor.capture();
 * executor.execute(() -> {
 *     try (MonitorScope scope = Monitor.openScope(snapshot)) {
 *         FlowLogContext.step("async_task", FlowEventType.ENTER);
 *         // ...
 *     }
 * });
 * }</pre>
 */
public final class FlowLogContext {

    private static final Logger log = LoggerFactory.getLogger(FlowLogContext.class);

    private static final String FLOW_ID_KEY = "flow.instanceId";
    private static final String FLOW_STEP_KEY = "flow.step";
    private static final String FLOW_STEP_NAME_KEY = "flow.stepName";
    private static final String FLOW_EVENT_TYPE_KEY = "flow.eventType";

    private FlowLogContext() {
    }

    /**
     * 启动一个新流程，生成 flowId 并写入 MDC。
     *
     * @param flowType 流程类型标识，如 "document_upload"
     * @param bizKey   业务主键，如用户ID、订单号
     * @return 生成的 flow instanceId
     */
    public static String beginFlow(String flowType, String bizKey) {
        if (Monitor.currentTraceId().isEmpty() && log.isDebugEnabled()) {
            log.debug("FlowLogContext.beginFlow without bound traceId; ensure Monitor.bindHttpIncoming or Filter runs first");
        }
        String flowId = flowType + "-" + bizKey + "-" + UUID.randomUUID().toString().substring(0, 8);
        MDC.put(FLOW_ID_KEY, flowId);
        MDC.put(FLOW_STEP_KEY, "0");
        return flowId;
    }

    /**
     * 记录一个步骤。步骤号自动递增。
     *
     * @param stepName  步骤名称，snake_case
     * @param eventType 事件类型
     */
    public static void step(String stepName, FlowEventType eventType) {
        int currentStep = 0;
        String existing = MDC.get(FLOW_STEP_KEY);
        if (existing != null) {
            try {
                currentStep = Integer.parseInt(existing);
            } catch (NumberFormatException ignored) {
            }
        }
        if (eventType == FlowEventType.ENTER) {
            currentStep++;
        }
        MDC.put(FLOW_STEP_KEY, String.valueOf(currentStep));
        MDC.put(FLOW_STEP_NAME_KEY, stepName);
        MDC.put(FLOW_EVENT_TYPE_KEY, eventType.name());
    }

    /**
     * 记录流程正常结束。
     */
    public static void endFlow() {
        step("end", FlowEventType.EXIT);
    }

    /**
     * 记录流程异常结束。
     *
     * @param stepName 发生异常的步骤
     * @param t        异常
     */
    public static void error(String stepName, Throwable t) {
        step(stepName, FlowEventType.ERROR);
        if (t != null) {
            MDC.put("flow.errorType", t.getClass().getSimpleName());
            MDC.put("flow.errorMessage", t.getMessage());
        }
    }

    /**
     * 清除所有 flow 相关的 MDC 字段（不调用 {@link Monitor#clear()}，避免误删 traceId）。
     */
    public static void clear() {
        MDC.remove(FLOW_ID_KEY);
        MDC.remove(FLOW_STEP_KEY);
        MDC.remove(FLOW_STEP_NAME_KEY);
        MDC.remove(FLOW_EVENT_TYPE_KEY);
        MDC.remove("flow.errorType");
        MDC.remove("flow.errorMessage");
    }

    /**
     * 流程事件类型。
     */
    public enum FlowEventType {
        ENTER,
        STATE_CHANGE,
        BRANCH,
        EXT_CALL,
        EXIT,
        ERROR
    }
}
