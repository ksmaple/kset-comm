package com.kset.common.monitor.autoconfigure;

import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.TraceSnapshot;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;


@AutoConfiguration
@ConditionalOnClass(EnableAsync.class)
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorAsyncAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "kset.monitor.async", name = "enabled", havingValue = "true", matchIfMissing = true)
    public TaskDecorator ksetMonitorTaskDecorator() {
        return runnable -> {
            TraceSnapshot snapshot = Monitor.capture();
            return () -> {
                TraceSnapshot previous = Monitor.capture();
                try {
                    Monitor.restore(snapshot);
                    runnable.run();
                } finally {
                    Monitor.restore(previous);
                }
            };
        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "kset.monitor.async", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ThreadPoolTaskExecutorCustomizer ksetMonitorThreadPoolTaskExecutorCustomizer(
            TaskDecorator ksetMonitorTaskDecorator) {
        return executor -> executor.setTaskDecorator(ksetMonitorTaskDecorator);
    }
}
