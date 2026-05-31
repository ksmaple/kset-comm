package com.kset.redis.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 * Redisson Fastjson2 编解码器，与 KSet RedisTemplate 使用同一套值序列化规则。
 */
public class KsetFastjsonRedissonCodec extends BaseCodec {

    private final KsetFastjsonRedisSerializer serializer;
    private final Encoder encoder;
    private final Decoder<Object> decoder;

    public KsetFastjsonRedissonCodec(KsetFastjsonRedisSerializer serializer) {
        this.serializer = java.util.Objects.requireNonNull(serializer, "serializer");
        this.encoder = value -> Unpooled.wrappedBuffer(this.serializer.serialize(value));
        this.decoder = (buf, state) -> {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            return this.serializer.deserialize(bytes);
        };
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}
