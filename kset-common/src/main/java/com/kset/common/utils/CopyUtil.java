package com.kset.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 对象深度拷贝：通过 JSON 序列化/反序列化生成与源对象引用独立的新实例。
 * <p>
 * 适用于 POJO、Map、List 等可 JSON 表达的结构；修改副本不会影响源对象。
 * 不支持不可序列化类型（如 InputStream、Thread 等）。
 */
public final class CopyUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private CopyUtil() {
    }

    /**
     * 深度拷贝为指定类型的全新对象。
     *
     * @param source     源对象，为 null 时返回 null
     * @param targetType 目标类型
     * @return 与源对象数据相同但引用独立的新实例
     */
    public static <T> T deepCopy(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        try {
            return MAPPER.convertValue(MAPPER.valueToTree(source), targetType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("深度拷贝失败: " + source.getClass().getName(), e);
        }
    }

    /**
     * 深度拷贝为泛型类型（如 {@code List<Order>}、{@code Map<String, User>}）。
     *
     * @param source     源对象，为 null 时返回 null
     * @param targetType 目标泛型类型
     * @return 与源对象数据相同但引用独立的新实例
     */
    public static <T> T deepCopy(Object source, TypeReference<T> targetType) {
        if (source == null) {
            return null;
        }
        try {
            return MAPPER.convertValue(MAPPER.valueToTree(source), targetType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("深度拷贝失败: " + source.getClass().getName(), e);
        }
    }
}
