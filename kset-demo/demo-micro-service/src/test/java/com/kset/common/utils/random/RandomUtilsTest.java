package com.kset.common.utils.random;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomUtilsTest {

    @Test
    void nextIntInclusiveShouldStayInRange() {
        for (int i = 0; i < 1000; i++) {
            int value = RandomUtils.nextIntInclusive(3, 7);
            assertTrue(value >= 3 && value <= 7);
        }
    }

    @Test
    void numberRandomShouldUseSameRange() {
        for (int i = 0; i < 500; i++) {
            int legacy = RandomUtils.numberRandom(1, 10);
            assertTrue(legacy >= 1 && legacy <= 10);
        }
    }

    @Test
    void shouldRejectInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.nextIntInclusive(5, 1));
    }
}
