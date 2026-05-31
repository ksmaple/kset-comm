package com.kset.common.utils.random;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class WeightedRandomReplayTest {

    @Test
    void shouldReplayJournalWithoutChangingLiveCounters() {
        InMemoryRandomStorage storage = new InMemoryRandomStorage();
        WeightedRandomEngine engine = WeightedRandomEngine.builder()
                .name("gacha")
                .weights(Map.of("A", 1d, "B", 1d))
                .algorithm(WeightedRandomAlgorithm.WHEEL)
                .journalEnabled(true)
                .metricsEnabled(true)
                .replayStore(storage)
                .build();

        List<String> live = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            live.add(engine.draw());
        }
        engine.flush();

        WeightedRandomReplayer replayer = engine.openReplaySession(storage, 1L);
        List<String> replayed = new ArrayList<>();
        while (replayer.hasNext()) {
            replayed.add(replayer.nextEvent().key());
        }
        assertEquals(live, replayed);

        long beforeReplayDraws = engine.getMetrics().getTotalDraws();
        replayer = engine.openReplaySession(storage, 1L);
        while (replayer.hasNext()) {
            replayer.nextEvent();
        }
        assertEquals(beforeReplayDraws, engine.getMetrics().getTotalDraws());
    }

    @Test
    void registryReplayShouldReadFromStore() {
        InMemoryRandomStorage storage = new InMemoryRandomStorage();
        KsetRandomRegistry registry = KsetRandomRegistry.getInstance();
        registry.setGlobalReplayStore(storage);
        registry.register("replay-biz", WeightedRandomConfig.builder()
                .weights(Map.of("X", 1d))
                .journalEnabled(true)
                .build());

        registry.draw("replay-biz");
        registry.draw("replay-biz");
        registry.flush("replay-biz");

        WeightedRandomReplayer replayer = registry.replay("replay-biz", 1L);
        assertEquals("X", replayer.nextEvent().key());
        assertEquals("X", replayer.nextEvent().key());
        assertFalse(replayer.hasNext());
    }
}
