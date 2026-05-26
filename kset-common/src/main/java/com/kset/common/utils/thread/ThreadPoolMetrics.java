package com.kset.common.utils.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 线程池运行时指标快照。
 *
 * <p>涵盖实时状态、累计统计、耗时分布及控制论反馈信息，
 * 可用于监控大屏、告警规则、动态调参决策等场景。</p>
 */
@Data
@Builder
public class ThreadPoolMetrics implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // === 配置 ===
    private String poolName;
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTimeMs;
    private int queueCapacity;
    private boolean autoTuneEnabled;
    private long targetLatencyMs;
    private boolean priorityQueueEnabled;
    private int defaultPriority;

    // === 实时状态 ===
    private int poolSize;
    private int activeCount;
    private int largestPoolSize;
    private int queueSize;
    private int queueRemainingCapacity;

    // === 累计指标 ===
    private long submittedTasks;
    private long completedTasks;
    private long failedTasks;
    private long rejectedTasks;

    // === 派生指标 ===
    /** 成功率 [0.0, 1.0] */
    private double successRate;
    /** 平均执行耗时（毫秒） */
    private double avgExecutionTimeMs;
    /** 平均等待耗时（毫秒） */
    private double avgWaitTimeMs;
    /** 最大执行耗时（毫秒） */
    private long maxExecutionTimeMs;
    /** 最小执行耗时（毫秒） */
    private long minExecutionTimeMs;
    /** P99 执行耗时（毫秒） */
    private double p99ExecutionTimeMs;
    /** 吞吐量（任务/秒） */
    private double throughputPerSecond;

    // === 控制论反馈 ===
    /** 延迟误差比例 = (实际平均耗时 - 目标耗时) / 目标耗时 */
    private double latencyErrorRatio;
    private long lastTuneTimeMs;
    private String lastTuneAction;

    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"failed to serialize metrics\"}";
        }
    }

    @Override
    public String toString() {
        return toJson();
    }
}
