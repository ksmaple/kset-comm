package com.kset.dubbo.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kset.cloud.spi.CloudRuleProvider;
import com.kset.cloud.spi.CloudRuleType;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DubboRouteRuleProvider implements CloudRuleProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CloudRuleType ruleType() {
        return CloudRuleType.DUBBO_ROUTE;
    }

    @Override
    public void onRuleChanged(String jsonContent) {
        if (jsonContent == null || jsonContent.isBlank()) {
            DubboRouteRuleHolder.resetToLocalDefault();
            return;
        }
        try {
            DubboRouteRuleHolder.RouteRuleConfig config =
                    objectMapper.readValue(jsonContent, DubboRouteRuleHolder.RouteRuleConfig.class);
            DubboRouteRuleHolder.update(config.getConditions());
            log.info("Dubbo route rules updated, conditions={}", DubboRouteRuleHolder.getConditions().size());
        } catch (Exception e) {
            log.warn("Failed to parse Dubbo route rules: {}", e.getMessage());
        }
    }
}
