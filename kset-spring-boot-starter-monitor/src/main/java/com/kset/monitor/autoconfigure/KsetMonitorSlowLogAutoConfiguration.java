package com.kset.monitor.autoconfigure;

import com.kset.monitor.config.KsetMonitorProperties;
import com.kset.monitor.web.SlowHttpMonitorFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
@ConditionalOnProperty(prefix = "kset.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "kset.monitor.slow-log", name = "http-enabled", havingValue = "true", matchIfMissing = true)
public class KsetMonitorSlowLogAutoConfiguration {

    @Bean
    public SlowHttpMonitorFilter slowHttpMonitorFilter(KsetMonitorProperties properties) {
        return new SlowHttpMonitorFilter(properties);
    }

    @Bean
    public FilterRegistrationBean<SlowHttpMonitorFilter> slowHttpMonitorFilterRegistration(
            SlowHttpMonitorFilter slowHttpMonitorFilter) {
        FilterRegistrationBean<SlowHttpMonitorFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(slowHttpMonitorFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        registration.setName("ksetSlowHttpMonitorFilter");
        return registration;
    }
}
