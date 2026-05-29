package com.kset.common.monitor.autoconfigure;

import com.kset.common.utils.thread.KsetThreadPoolFactory;
import com.kset.common.utils.thread.MdcThreadPoolTraceAdapter;
import com.kset.common.monitor.config.KsetMonitorProperties;
import com.kset.common.monitor.thread.MonitorThreadPoolReporter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorThreadPoolAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "kset.monitor.thread-pool", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class ThreadPoolMonitorConfiguration implements ApplicationListener<ApplicationReadyEvent> {

        private final KsetMonitorProperties properties;

        ThreadPoolMonitorConfiguration(KsetMonitorProperties properties) {
            this.properties = properties;
        }

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            KsetThreadPoolFactory factory = KsetThreadPoolFactory.getInstance();
            KsetMonitorProperties.ThreadPool threadPool = properties.getThreadPool();
            if (threadPool.isTracePropagationEnabled()) {
                factory.setGlobalTraceContextAdapter(new MdcThreadPoolTraceAdapter());
            }
            factory.setGlobalReporter(new MonitorThreadPoolReporter());
        }
    }
}
