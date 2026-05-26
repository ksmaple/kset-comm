package com.kset.common.utils.thread;

/**
 * 线程池指标与事件上报接口，由用户侧实现自定义上报逻辑。
 *
 * <p>支持指标上报（任务执行耗时、吞吐量、延迟误差等）和异常上报（任务失败、拒绝策略触发、线程异常等）。
 * 所有方法均携带 traceId 参数，便于与分布式链路追踪框架（如 SkyWalking、OpenTelemetry、SLF4J MDC 等）对接。</p>
 *
 * <p>实现类应保证方法执行轻量，避免阻塞线程池主流程。若需异步上报，可在实现中自行投递到队列或线程。</p>
 */
public interface ThreadPoolReporter {

    /**
     * 任务提交时触发。
     *
     * @param poolName 线程池名称
     * @param traceId  链路追踪 ID（若框架未注入则为 null）
     * @param task     任务对象
     */
    void onTaskSubmitted(String poolName, String traceId, Runnable task);

    /**
     * 任务开始执行时触发（已从队列取出，等待时间已确定）。
     *
     * @param poolName   线程池名称
     * @param traceId    链路追踪 ID
     * @param task       任务对象
     * @param waitTimeMs 任务在队列中等待的时间（毫秒）
     */
    void onTaskStarted(String poolName, String traceId, Runnable task, long waitTimeMs);

    /**
     * 任务执行完成时触发（无论成功或失败）。
     *
     * @param poolName        线程池名称
     * @param traceId         链路追踪 ID
     * @param task            任务对象
     * @param executionTimeMs 实际执行耗时（毫秒）
     * @param success         是否成功（无异常且 Future 未抛 ExecutionException）
     */
    void onTaskCompleted(String poolName, String traceId, Runnable task, long executionTimeMs, boolean success);

    /**
     * 任务被拒绝时触发（拒绝策略执行前）。
     *
     * @param poolName   线程池名称
     * @param traceId    链路追踪 ID
     * @param task       被拒绝的任务
     * @param policyName 拒绝策略名称（如 AbortPolicy、CallerRunsPolicy）
     */
    void onTaskRejected(String poolName, String traceId, Runnable task, String policyName);

    /**
     * 控制论自动调节触发时上报。
     *
     * @param poolName 线程池名称
     * @param traceId  链路追踪 ID（调节线程的 traceId，可能为 null）
     * @param action   调节动作描述（如 expand_core:4->8）
     * @param metrics  调节前的指标快照
     */
    void onAutoTuned(String poolName, String traceId, String action, ThreadPoolMetrics metrics);

    /**
     * 线程池执行过程中发生异常时触发。
     *
     * <p>包括任务执行抛出的未捕获异常、afterExecute 中检测到的 Future 异常等。</p>
     *
     * @param poolName 线程池名称
     * @param traceId  链路追踪 ID
     * @param task     执行中的任务（可能为 null）
     * @param t        异常对象
     */
    void onError(String poolName, String traceId, Runnable task, Throwable t);

    /**
     * 定时/周期性的指标全量上报。
     *
     * <p>由线程池在控制论调节周期或用户手动触发时调用，用于上报完整指标到大屏、Prometheus、日志平台等。</p>
     *
     * @param poolName 线程池名称
     * @param traceId  链路追踪 ID（可能为 null）
     * @param metrics  完整指标快照
     */
    void onMetricsReport(String poolName, String traceId, ThreadPoolMetrics metrics);
}
