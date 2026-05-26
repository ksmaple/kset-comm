package com.kset.nacos.registrar;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.kset.cloud.config.KsetCloudProperties;
import com.kset.cloud.nacos.NacosConfigConvention;
import com.kset.cloud.spi.CloudRuleProvider;
import com.kset.cloud.spi.CloudRuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.Executor;

@AutoConfiguration
@ConditionalOnClass(ConfigService.class)
public class KsetCloudRuleRegistrarAutoConfiguration {

    @Bean
    public KsetCloudRuleRegistrar ksetCloudRuleRegistrar(ObjectProvider<ConfigService> configServiceProvider,
                                                         ObjectProvider<List<CloudRuleProvider>> providers,
                                                         KsetCloudProperties properties,
                                                         NacosConfigConvention convention,
                                                         Environment environment) {
        return new KsetCloudRuleRegistrar(configServiceProvider, providers.getIfAvailable(List::of),
                properties, convention, environment);
    }

    static class KsetCloudRuleRegistrar {

        private static final Logger log = LoggerFactory.getLogger(KsetCloudRuleRegistrar.class);

        KsetCloudRuleRegistrar(ObjectProvider<ConfigService> configServiceProvider,
                               List<CloudRuleProvider> providers,
                               KsetCloudProperties properties,
                               NacosConfigConvention convention,
                               Environment environment) {
            ConfigService configService = configServiceProvider.getIfAvailable();
            if (configService == null || providers.isEmpty()) {
                return;
            }

            String appName = environment.getProperty("spring.application.name", "application");
            String group = convention.group();

            registerListener(configService, group, convention.dubboRouteDataId(appName),
                    CloudRuleType.DUBBO_ROUTE, providers);
            registerListener(configService, group, convention.gatewayRouteDataId(appName),
                    CloudRuleType.GATEWAY_ROUTE, providers);
        }

        private void registerListener(ConfigService configService, String group, String dataId,
                                      CloudRuleType type, List<CloudRuleProvider> providers) {
            try {
                configService.addListener(dataId, group, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        providers.stream()
                                .filter(p -> p.ruleType() == type)
                                .forEach(p -> p.onRuleChanged(configInfo));
                    }
                });
                log.info("Registered CloudRuleProvider listener: dataId={}, type={}", dataId, type);
            } catch (NacosException e) {
                log.warn("Failed to register CloudRuleProvider listener for dataId={}: {}", dataId, e.getMessage());
            }
        }
    }
}
