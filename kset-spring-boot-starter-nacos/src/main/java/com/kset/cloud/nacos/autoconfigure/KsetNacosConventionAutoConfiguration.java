package com.kset.cloud.nacos.autoconfigure;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.cloud.nacos.NacosConfigConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosConfigAutoConfiguration")
@EnableConfigurationProperties(KsetCloudProperties.class)
public class KsetNacosConventionAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KsetNacosConventionAutoConfiguration.class);

    @Bean
    public KsetNacosConventionInitializer ksetNacosConventionInitializer(KsetCloudProperties properties,
                                                                         NacosConfigConvention convention) {
        return new KsetNacosConventionInitializer(properties, convention);
    }

    static class KsetNacosConventionInitializer {

        KsetNacosConventionInitializer(KsetCloudProperties properties, NacosConfigConvention convention) {
            log.info("KSet Nacos convention enabled: group={}, namespace={}, commonConfig={}",
                    convention.group(), convention.namespace(), convention.commonConfigDataId());
        }
    }
}
