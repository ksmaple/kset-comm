package com.kset.monitor.autoconfigure;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.monitor.gateway.TraceIdGatewayFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.cloud.gateway.filter.GatewayFilterChain")
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "kset.monitor.gateway", name = "trace-enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorGatewayAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "traceIdGatewayFilter")
    public GlobalFilter traceIdGatewayFilter(KsetCloudProperties properties) {
        return new TraceIdGatewayFilter(properties);
    }
}
