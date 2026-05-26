package com.kset.common.utils.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 固定种子的加权随机盒子，同种子下序列可复现；采用整数配额算法，适合极小概率场景。
 *
 * <p>薄封装 {@link WeightedRandomEngine}，不累积指标。
 */
public final class SeededRandomBox {

    private static final int DEFAULT_SAMPLE_BASE = 100_000;

    private final WeightedRandomEngine engine;
    private final List<String> hiddenKeys = new ArrayList<>();

    private SeededRandomBox(long seed, int sampleBase, Map<String, Double> probabilities) {
        this.engine = WeightedRandomEngine.builder()
                .weights(new HashMap<>(probabilities))
                .seed(seed)
                .sampleBase(sampleBase)
                .algorithm(WeightedRandomAlgorithm.QUOTA)
                .metricsEnabled(false)
                .build();
    }

    public static SeededRandomBox of(long seed, Map<String, Double> probabilities) {
        return of(seed, DEFAULT_SAMPLE_BASE, probabilities);
    }

    public static SeededRandomBox of(long seed, int sampleBase, Map<String, Double> probabilities) {
        if (probabilities == null || probabilities.isEmpty()) {
            throw new IllegalArgumentException("概率配置不能为空");
        }
        if (sampleBase <= 0) {
            throw new IllegalArgumentException("sampleBase 必须大于 0: " + sampleBase);
        }
        return new SeededRandomBox(seed, sampleBase, probabilities);
    }

    public static SeededRandomBox build(long seed, Map<String, Double> probabilities) {
        return of(seed, probabilities);
    }

    public SeededRandomBox hidden(String key) {
        hiddenKeys.add(key);
        return this;
    }

    public String random() {
        return engine.draw(hiddenKeys.toArray(String[]::new));
    }
}
