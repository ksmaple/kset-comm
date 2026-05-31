package com.kset.common.utils.random;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomBoxTest {

    @Test
    void shouldRespectHiddenKeys() {
        Map<String, Double> weights = Map.of("A", 1d, "B", 1d);
        for (int i = 0; i < 50; i++) {
            assertEquals("A", RandomBox.of(weights).hidden("B").random());
        }
    }

    @Test
    void shouldPreferHigherWeight() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("heavy", 0.99d);
        weights.put("light", 0.01d);

        int heavyCount = 0;
        for (int i = 0; i < 500; i++) {
            if ("heavy".equals(RandomBox.of(weights).random())) {
                heavyCount++;
            }
        }
        assertTrue(heavyCount > 400);
    }

    @Test
    void shouldRejectEmptyPool() {
        assertThrows(IllegalArgumentException.class, () -> RandomBox.of(Map.of("A", 0d)).random());
    }
}
