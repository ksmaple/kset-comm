package com.kset.cloud.spi;

import com.kset.cloud.config.KsetCloudProperties;

/**
 * 默认灰度标签解析：透传请求头，无则使用配置默认值
 */
public class DefaultGrayTagResolver implements GrayTagResolver {

    private final KsetCloudProperties properties;

    public DefaultGrayTagResolver(KsetCloudProperties properties) {
        this.properties = properties;
    }

    @Override
    public String resolve(String grayHeaderValue) {
        if (grayHeaderValue != null && !grayHeaderValue.isBlank()) {
            return grayHeaderValue.trim();
        }
        return properties.getDubbo().getDefaultGrayTag();
    }
}
