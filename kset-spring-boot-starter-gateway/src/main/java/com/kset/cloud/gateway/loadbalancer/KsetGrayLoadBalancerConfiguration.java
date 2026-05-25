package com.kset.cloud.gateway.loadbalancer;

import com.kset.cloud.config.KsetCloudProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KsetGrayLoadBalancerConfiguration {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> ksetGrayLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory,
            KsetCloudProperties properties) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new KsetGrayLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                properties);
    }
}
