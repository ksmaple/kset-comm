package com.kset.common.monitor;

/**
 * Dubbo attachment 读写抽象，避免 common 模块依赖 Dubbo API。
 */
public interface DubboAttachmentAccessor {

    String getAttachment(String key);

    void setAttachment(String key, String value);
}
