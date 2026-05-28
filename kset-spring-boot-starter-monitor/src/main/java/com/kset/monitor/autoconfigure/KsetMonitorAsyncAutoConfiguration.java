package com.kset.monitor.autoconfigure;

import com.kset.common.monitor.KsetMonitor;
import com.kset.common.monitor.TraceSnapshot;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * {@code @Async} 链路上下文传播：为 Spring 托管的 {@link ThreadPoolTaskExecutor} 注入 TaskDecorator。
 */
@AutoConfiguration
@ConditionalOnClass(EnableAsync.class)
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "kset.monitor.async", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorAsyncAutoConfiguration {

    @Bean
    public TaskDecorator ksetMonitorTaskDecorator() {
        return runnable -> {
            TraceSnapshot snapshot = KsetMonitor.capture();
            return () -> {
                TraceSnapshot previous = KsetMonitor.capture();
                try {
                    KsetMonitor.restore(snapshot);
                    runnable.run();
                } finally {
                    KsetMonitor.restore(previous);
                }
            };
        };
    }

    @Bean
    public ThreadPoolTaskExecutorCustomizer ksetMonitorThreadPoolTaskExecutorCustomizer(
            TaskDecorator ksetMonitorTaskDecorator) {
        return executor -> {
            TaskDecorator existing = executor.getTaskDecorator();
            if (existing == null) {
                executor.setTaskDecorator(ksetMonitorTaskDecorator);
            } else {
                executor.setTaskDecorator(runnable ->
                        ksetMonitorTaskDecorator.decorate(existing.decorate(runnable)));
            }
        };
    }
}
