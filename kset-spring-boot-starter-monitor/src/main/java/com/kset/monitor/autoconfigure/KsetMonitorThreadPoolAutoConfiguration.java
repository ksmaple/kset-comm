package com.kset.monitor.autoconfigure;

import com.kset.common.utils.thread.KsetThreadPoolFactory;
import com.kset.common.utils.thread.MdcThreadPoolTraceAdapter;
import com.kset.monitor.config.KsetMonitorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@AutoConfiguration
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "kset.monitor.thread-pool", name = "trace-propagation-enabled",
        havingValue = "true", matchIfMissing = true)
public class KsetMonitorThreadPoolAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        KsetThreadPoolFactory.getInstance().setGlobalTraceContextAdapter(new MdcThreadPoolTraceAdapter());
    }
}
