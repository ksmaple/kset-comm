package com.kset.redis.autoconfigure;

import com.kset.redis.core.KsetRedisService;
import com.kset.redis.codec.KsetFastjsonRedisSerializer;
import com.kset.redis.config.KsetRedisSerializerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class KsetRedisAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisAutoConfiguration.class, KsetRedisAutoConfiguration.class))
            .withBean(RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class));

    @Test
    void keepsNativeRedisBeansAndAddsKsetRedisTemplate() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("redisTemplate");
            assertThat(context).hasBean("stringRedisTemplate");
            assertThat(context).hasBean("ksetRedisTemplate");
            assertThat(context).hasBean(KsetRedisSerializerConfiguration.BEAN_NAME);
            assertThat(context).hasSingleBean(StringRedisTemplate.class);
            assertThat(context).hasSingleBean(KsetRedisService.class);
            assertThat(context.getBean(KsetRedisService.class).template())
                    .isSameAs(context.getBean("ksetRedisTemplate", RedisTemplate.class));
            assertThat(context.getBean("redisTemplate"))
                    .isNotSameAs(context.getBean("ksetRedisTemplate"));
        });
    }

    @Test
    void allowsOverridingKsetRedisValueSerializer() {
        KsetFastjsonRedisSerializer customSerializer = new KsetFastjsonRedisSerializer();

        contextRunner
                .withBean(KsetRedisSerializerConfiguration.BEAN_NAME,
                        KsetFastjsonRedisSerializer.class,
                        () -> customSerializer)
                .run(context -> assertThat(context.getBean(KsetRedisSerializerConfiguration.BEAN_NAME))
                        .isSameAs(customSerializer));
    }
}
