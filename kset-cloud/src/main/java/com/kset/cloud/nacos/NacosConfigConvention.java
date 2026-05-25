package com.kset.cloud.nacos;

import com.kset.cloud.config.KsetCloudProperties;

/**
 * Nacos 配置命名约定
 */
public class NacosConfigConvention {

    public static final String COMMON_CONFIG_DATA_ID = "kset-common.yaml";

    private final KsetCloudProperties properties;

    public NacosConfigConvention(KsetCloudProperties properties) {
        this.properties = properties;
    }

    public String group() {
        return properties.getNacos().getGroup();
    }

    public String namespace() {
        return properties.getNacos().getNamespace();
    }

    public String appConfigDataId(String appName) {
        return appName + ".yaml";
    }

    public String commonConfigDataId() {
        return properties.getNacos().getCommonConfigDataId();
    }

    public String flowRuleDataId(String appName) {
        return appName + "-flow-rules";
    }

    public String degradeRuleDataId(String appName) {
        return appName + "-degrade-rules";
    }

    public String paramFlowRuleDataId(String appName) {
        return appName + "-param-flow-rules";
    }

    public String dubboRouteDataId(String appName) {
        return appName + "-route-rules";
    }

    public String gatewayRouteDataId(String appName) {
        return appName + "-gateway-routes";
    }

    public String gatewayFlowRuleDataId(String appName) {
        return appName + "-gateway-flow-rules";
    }
}
