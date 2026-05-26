package com.kset.common.utils.thread;

/**
 * 链路上下文适配器接口，用于在任务提交线程和执行线程之间传递 traceId。
 *
 * <p>典型场景：</p>
 * <ul>
 *   <li>提交任务时：调用 {@link #getTraceId()} 从当前线程（如 Servlet 线程）获取 traceId</li>
 *   <li>执行任务前：调用 {@link #setTraceId(String)} 将 traceId 注入执行线程（如 MDC.put）</li>
 *   <li>执行任务后：调用 {@link #clear()} 清理执行线程的上下文（如 MDC.remove）</li>
 * </ul>
 */
public interface ThreadPoolTraceAdapter {

    /**
     * 从当前线程获取 traceId。
     *
     * @return traceId，若当前线程无上下文则返回 null
     */
    String getTraceId();

    /**
     * 将 traceId 设置到当前线程上下文。
     *
     * @param traceId 链路追踪 ID
     */
    void setTraceId(String traceId);

    /**
     * 清理当前线程的 traceId 上下文。
     */
    void clear();
}
