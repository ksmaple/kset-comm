package com.kset.common.monitor.reporter;

/**
 * 异步上报队列 SPI。
 */
public interface AsyncReporter {

    void report(Runnable task);

    void shutdown();
}
