package com.kset.cloud.spi;

/**
 * 云服务规则变更 SPI，业务可实现自定义规则解析逻辑。
 */
public interface CloudRuleProvider {

    CloudRuleType ruleType();

    void onRuleChanged(String jsonContent);
}
