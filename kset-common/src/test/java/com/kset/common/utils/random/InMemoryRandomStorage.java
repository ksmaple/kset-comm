package com.kset.common.utils.random;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 内存版 Persistence + ReplayStore，仅供单测与本地调试。
 */
public class InMemoryRandomStorage implements WeightedRandomPersistence, WeightedRandomReplayStore {

    private final Map<String, WeightedRandomConfig> configs = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Long>> counters = new ConcurrentHashMap<>();
    private final Map<String, List<DrawEvent>> journals = new ConcurrentHashMap<>();

    @Override
    public Optional<WeightedRandomConfig> loadConfig(String name) {
        return Optional.ofNullable(configs.get(name));
    }

    @Override
    public void saveConfig(String name, WeightedRandomConfig config) {
        configs.put(name, config);
    }

    @Override
    public Map<String, Long> loadCounters(String name) {
        return new LinkedHashMap<>(counters.getOrDefault(name, Map.of()));
    }

    @Override
    public void saveCounters(String name, Map<String, Long> counterMap) {
        counters.put(name, new LinkedHashMap<>(counterMap));
    }

    @Override
    public long loadLatestSeq(String name) {
        List<DrawEvent> events = journals.get(name);
        if (events == null || events.isEmpty()) {
            return 0L;
        }
        return events.stream().mapToLong(DrawEvent::seq).max().orElse(0L);
    }

    @Override
    public void appendDrawEvents(String name, List<DrawEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        journals.computeIfAbsent(name, ignored -> new CopyOnWriteArrayList<>()).addAll(events);
    }

    @Override
    public List<DrawEvent> loadDrawEvents(String name, long fromSeq, int limit) {
        List<DrawEvent> events = journals.getOrDefault(name, List.of());
        return events.stream()
                .filter(event -> event.seq() >= fromSeq)
                .sorted(Comparator.comparingLong(DrawEvent::seq))
                .limit(limit <= 0 ? Long.MAX_VALUE : limit)
                .toList();
    }

    public List<DrawEvent> allEvents(String name) {
        return new ArrayList<>(journals.getOrDefault(name, List.of()));
    }
}
