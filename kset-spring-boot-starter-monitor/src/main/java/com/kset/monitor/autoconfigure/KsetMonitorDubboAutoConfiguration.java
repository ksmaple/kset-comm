package com.kset.monitor.autoconfigure;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.monitor.dubbo.DubboTraceFilter;
import org.apache.dubbo.rpc.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Filter.class)
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "kset.monitor.dubbo", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorDubboAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "dubboTraceFilter")
    public Filter dubboTraceFilter(KsetCloudProperties properties) {
        return new DubboTraceFilter(properties);
    }
}
