package com.kset.common.monitor.autoconfigure;

import com.kset.common.utils.thread.KsetThreadPoolFactory;
import com.kset.common.utils.thread.MdcThreadPoolTraceAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorThreadPoolAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "kset.monitor.thread-pool", name = "trace-propagation-enabled",
            havingValue = "true", matchIfMissing = true)
    static class TracePropagationConfiguration implements ApplicationListener<ApplicationReadyEvent> {

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            KsetThreadPoolFactory.getInstance().setGlobalTraceContextAdapter(new MdcThreadPoolTraceAdapter());
        }
    }
}
