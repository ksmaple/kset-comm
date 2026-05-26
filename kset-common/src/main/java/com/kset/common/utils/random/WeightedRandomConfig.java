package com.kset.common.utils.random;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 加权随机引擎配置。
 */
@Data
@Builder
public class WeightedRandomConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_SAMPLE_BASE = 100_000;
    private static final int DEFAULT_JOURNAL_BUFFER_SIZE = 100;

    private String name;
    @Builder.Default
    private Map<String, Double> weights = new LinkedHashMap<>();
    private List<String> items;
    private WeightedRandomAlgorithm algorithm;
    private Long seed;
    @Builder.Default
    private int sampleBase = DEFAULT_SAMPLE_BASE;
    @Builder.Default
    private boolean metricsEnabled = true;
    @Builder.Default
    private boolean replayEnabled = false;
    @Builder.Default
    private boolean journalEnabled = false;
    @Builder.Default
    private int journalBufferSize = DEFAULT_JOURNAL_BUFFER_SIZE;

    public Map<String, Double> resolvedWeights() {
        if (weights != null && !weights.isEmpty()) {
            return Collections.unmodifiableMap(new LinkedHashMap<>(weights));
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("weights 或 items 至少配置一项");
        }
        Map<String, Double> uniform = new LinkedHashMap<>();
        for (String item : items) {
            uniform.put(item, 1d);
        }
        return uniform;
    }

    public WeightedRandomAlgorithm resolvedAlgorithm() {
        if (algorithm != null) {
            return algorithm;
        }
        Map<String, Double> w = resolvedWeights();
        if (isUniformWeights(w)) {
            return WeightedRandomAlgorithm.UNIFORM;
        }
        if (seed != null) {
            return WeightedRandomAlgorithm.QUOTA;
        }
        return WeightedRandomAlgorithm.WHEEL;
    }

    private static boolean isUniformWeights(Map<String, Double> w) {
        if (w.size() <= 1) {
            return true;
        }
        Double first = null;
        for (Double value : w.values()) {
            if (first == null) {
                first = value;
            } else if (Double.compare(first, value) != 0) {
                return false;
            }
        }
        return true;
    }
}
