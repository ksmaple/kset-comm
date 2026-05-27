package com.kset.common.utils.random;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightedRandomEngineTest {

    @Test
    void shouldDrawWithWheelAlgorithm() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("heavy", 0.99d);
        weights.put("light", 0.01d);

        WeightedRandomEngine engine = WeightedRandomEngine.builder()
                .name("wheel-test")
                .weights(weights)
                .algorithm(WeightedRandomAlgorithm.WHEEL)
                .metricsEnabled(true)
                .build();

        int heavyCount = 0;
        for (int i = 0; i < 500; i++) {
            if ("heavy".equals(engine.draw())) {
                heavyCount++;
            }
        }
        assertTrue(heavyCount > 400);
        assertEquals(500, engine.getMetrics().getTotalDraws());
    }

    @Test
    void shouldRespectHiddenKeys() {
        WeightedRandomEngine engine = WeightedRandomEngine.builder()
                .weights(Map.of("A", 1d, "B", 1d))
                .algorithm(WeightedRandomAlgorithm.WHEEL)
                .build();

        for (int i = 0; i < 20; i++) {
            assertEquals("A", engine.draw("B"));
        }
    }

    @Test
    void shouldPreviewSequenceWithSeed() {
        Map<String, Double> weights = Map.of(
                "1", 0.791d,
                "2", 0.09d,
                "3", 0.07d,
                "4", 0.03d,
                "5", 0.01d,
                "6", 0.00755d,
                "8", 0.00012d
        );

        WeightedRandomEngine engine = WeightedRandomEngine.builder()
                .weights(weights)
                .seed(34L)
                .algorithm(WeightedRandomAlgorithm.QUOTA)
                .replayEnabled(true)
                .build();

        List<String> preview = engine.previewSequence(5);
        List<String> live = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            live.add(engine.draw());
        }
        assertEquals(preview, live);
    }

    @Test
    void shouldReplayFromSeedWithoutAdvancingLiveEngine() {
        WeightedRandomEngine engine = WeightedRandomEngine.builder()
                .weights(Map.of("A", 1d, "B", 1d))
                .seed(99L)
                .algorithm(WeightedRandomAlgorithm.QUOTA)
                .replayEnabled(true)
                .build();

        String first = engine.draw();
        String replayThird = engine.replayFromSeed(3);
        String second = engine.draw();
        assertEquals(engine.replayFromSeed(2), second);
        assertEquals(engine.replayFromSeed(1), first);
        assertEquals(replayThird, engine.replayFromSeed(3));
    }

    @Test
    void shouldRejectPreviewWhenReplayDisabled() {
        WeightedRandomEngine engine = WeightedRandomEngine.builder()
                .weights(Map.of("A", 1d))
                .seed(1L)
                .replayEnabled(false)
                .build();
        assertThrows(IllegalStateException.class, () -> engine.previewSequence(1));
    }
}
