package com.kset.monitor.autoconfigure;

import com.kset.common.monitor.KsetMonitor;
import com.kset.common.monitor.KsetMonitorFacade;
import com.kset.monitor.internal.MdcMonitorFacade;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorFacadeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KsetMonitorFacade ksetMonitorFacade() {
        return new MdcMonitorFacade();
    }

    @Bean
    public Object ksetMonitorFacadeInstaller(KsetMonitorFacade facade) {
        KsetMonitor.install(facade);
        return new Object();
    }
}
