package com.kset.common.utils.random;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeightedRandomPersistenceTest {

    @Test
    void shouldLoadAndSaveConfigAndCounters() {
        InMemoryRandomStorage storage = new InMemoryRandomStorage();
        WeightedRandomConfig config = WeightedRandomConfig.builder()
                .name("persist-test")
                .weights(Map.of("A", 0.7d, "B", 0.3d))
                .algorithm(WeightedRandomAlgorithm.WHEEL)
                .build();
        storage.saveConfig("persist-test", config);

        WeightedRandomEngine engine = WeightedRandomEngine.builder()
                .config(config)
                .persistence(storage)
                .build();
        engine.draw();
        engine.draw();
        engine.flush();

        Map<String, Long> counters = storage.loadCounters("persist-test");
        assertEquals(2L, counters.values().stream().mapToLong(Long::longValue).sum());
        assertTrue(storage.loadConfig("persist-test").isPresent());
    }
}
