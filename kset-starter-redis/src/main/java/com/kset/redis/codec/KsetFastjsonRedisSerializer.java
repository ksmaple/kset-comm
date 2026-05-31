package com.kset.redis.codec;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * KSet Redis 值序列化器：基础类型使用 Redis 友好文本，复杂对象使用 Fastjson2。
 */
public class KsetFastjsonRedisSerializer implements RedisSerializer<Object> {

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        if (isBasicValue(value)) {
            return basicValueToString(value).getBytes(StandardCharsets.UTF_8);
        }
        return JSON.toJSONBytes(value, JSONWriter.Feature.WriteClassName, JSONWriter.Feature.WriteMapNullValue);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String text = new String(bytes, StandardCharsets.UTF_8);
        if (looksLikeJson(text)) {
            return JSON.parse(text, JSONReader.Feature.SupportAutoType);
        }
        return parseBasicValue(text);
    }

    public static boolean isBasicType(Class<?> type) {
        return type.isPrimitive()
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class == type
                || Character.class == type
                || Enum.class.isAssignableFrom(type);
    }

    private static boolean isBasicValue(Object value) {
        return isBasicType(value.getClass());
    }

    private static String basicValueToString(Object value) {
        if (value instanceof Enum<?> enumValue) {
            return enumValue.name();
        }
        return String.valueOf(value);
    }

    private static Object parseBasicValue(String text) {
        if ("true".equalsIgnoreCase(text)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(text)) {
            return Boolean.FALSE;
        }
        if (text.matches("-?\\d+")) {
            try {
                return Long.valueOf(text);
            } catch (NumberFormatException ex) {
                return new BigInteger(text);
            }
        }
        if (text.matches("-?\\d+\\.\\d+")) {
            return new BigDecimal(text);
        }
        return text;
    }

    private static boolean looksLikeJson(String text) {
        String value = text.trim();
        return (value.startsWith("{") && value.endsWith("}"))
                || (value.startsWith("[") && value.endsWith("]"));
    }
}
