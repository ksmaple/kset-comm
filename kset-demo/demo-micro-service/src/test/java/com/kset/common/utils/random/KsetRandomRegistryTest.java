package com.kset.common.utils.random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KsetRandomRegistryTest {

    private KsetRandomRegistry registry;

    @BeforeEach
    void setUp() {
        registry = KsetRandomRegistry.getInstance();
    }

    @Test
    void shouldIsolateBizPools() {
        registry.register("biz-a", WeightedRandomConfig.builder()
                .weights(Map.of("A", 1d))
                .build());
        registry.register("biz-b", WeightedRandomConfig.builder()
                .weights(Map.of("B", 1d))
                .build());

        assertEquals("A", registry.draw("biz-a"));
        assertEquals("B", registry.draw("biz-b"));
    }

    @Test
    void shouldNotifyGlobalObserver() {
        AtomicInteger drawCount = new AtomicInteger();
        registry.setGlobalObserver(new WeightedRandomObserver() {
            @Override
            public void onDraw(String name, String key, long totalDraws) {
                drawCount.incrementAndGet();
            }
        });

        registry.register("obs-test", WeightedRandomConfig.builder()
                .weights(Map.of("X", 1d))
                .build());
        registry.draw("obs-test");

        assertEquals(1, drawCount.get());
    }
}
