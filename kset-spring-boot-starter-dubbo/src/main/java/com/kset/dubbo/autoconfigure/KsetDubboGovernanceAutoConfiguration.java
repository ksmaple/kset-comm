package com.kset.dubbo.autoconfigure;

import com.kset.dubbo.route.DubboRouteRuleProvider;
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
    public DubboRouteRuleProvider dubboRouteRuleProvider() {
        return new DubboRouteRuleProvider();
    }
}
