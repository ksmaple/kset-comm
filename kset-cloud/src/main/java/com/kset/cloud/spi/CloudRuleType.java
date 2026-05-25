package com.kset.cloud.spi;

/**
 * 云服务规则类型
 */
public enum CloudRuleType {

    SENTINEL_FLOW,
    SENTINEL_DEGRADE,
    SENTINEL_PARAM_FLOW,
    DUBBO_ROUTE,
    GATEWAY_ROUTE,
    CUSTOM
}
