package com.kset.cloud.dubbo.autoconfigure;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.cloud.dubbo.filter.DubboTraceFilter;
import com.kset.cloud.dubbo.route.DubboRouteRuleProvider;
import org.apache.dubbo.rpc.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnClass(Filter.class)
@Import(DubboRouteRuleProvider.class)
public class KsetDubboGovernanceAutoConfiguration {

    @Bean
    public Filter dubboTraceFilter(KsetCloudProperties properties) {
        return new DubboTraceFilter(properties);
    }

    @Bean
    public DubboRouteRuleProvider dubboRouteRuleProvider() {
        return new DubboRouteRuleProvider();
    }
}
