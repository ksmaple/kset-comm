package com.kset.common.utils.random;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeededRandomBoxTest {

    @Test
    void sameSeedShouldProduceSameSequence() {
        Map<String, Double> weights = Map.of(
                "1", 0.791d,
                "2", 0.09d,
                "3", 0.07d,
                "4", 0.03d,
                "5", 0.01d,
                "6", 0.00755d,
                "8", 0.00012d
        );

        SeededRandomBox a = SeededRandomBox.of(34L, weights);
        SeededRandomBox b = SeededRandomBox.of(34L, weights);

        for (int i = 0; i < 20; i++) {
            assertEquals(a.random(), b.random());
        }
    }
}
