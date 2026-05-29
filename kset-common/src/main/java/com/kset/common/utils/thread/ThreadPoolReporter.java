package com.kset.common.utils.thread;


public interface ThreadPoolReporter {

    
    void onTaskSubmitted(String poolName, String traceId, Runnable task);

    
    void onTaskStarted(String poolName, String traceId, Runnable task, long waitTimeMs);

    
    void onTaskCompleted(String poolName, String traceId, Runnable task, long executionTimeMs, boolean success);

    
    void onTaskRejected(String poolName, String traceId, Runnable task, String policyName);

    
    void onAutoTuned(String poolName, String traceId, String action, ThreadPoolMetrics metrics);

    
    void onError(String poolName, String traceId, Runnable task, Throwable t);

    
    void onMetricsReport(String poolName, String traceId, ThreadPoolMetrics metrics);
}
