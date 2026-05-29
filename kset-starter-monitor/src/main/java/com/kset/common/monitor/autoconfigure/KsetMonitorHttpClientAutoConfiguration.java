package com.kset.common.monitor.autoconfigure;

import com.kset.common.monitor.config.KsetMonitorProperties;
import com.kset.common.utils.http.HttpLogInterceptor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "okhttp3.Interceptor")
public class KsetMonitorHttpClientAutoConfiguration {

    @Bean
    ApplicationRunner ksetMonitorHttpClientConfigurer(KsetMonitorProperties properties) {
        return args -> HttpLogInterceptor.setMonitorEnabled(
                properties.isEnabled() && properties.getHttpClient().isEnabled());
    }
}
