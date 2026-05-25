package com.kset.cloud.spi;

/**
 * 灰度标签解析 SPI，从 HTTP 请求中解析灰度标识。
 */
public interface GrayTagResolver {

    /**
     * @param grayHeaderValue 灰度请求头原始值，可能为 null
     * @return 解析后的灰度标签，null 表示使用默认标签
     */
    String resolve(String grayHeaderValue);
}
