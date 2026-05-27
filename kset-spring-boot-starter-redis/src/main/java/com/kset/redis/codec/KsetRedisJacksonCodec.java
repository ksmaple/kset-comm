package com.kset.redis.codec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.redisson.codec.JsonJacksonCodec;

/**
 * Redisson 通用 Jackson 编解码器（与框架 JSON 序列化风格一致）。
 */
public class KsetRedisJacksonCodec extends JsonJacksonCodec {

    private static final ObjectMapper SHARED_MAPPER = createMapper();

    public KsetRedisJacksonCodec() {
        super(SHARED_MAPPER);
    }

    public static ObjectMapper sharedObjectMapper() {
        return SHARED_MAPPER;
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return mapper;
    }
}
