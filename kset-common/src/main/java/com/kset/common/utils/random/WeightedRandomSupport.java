package com.kset.common.utils.random;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 加权随机抽取的内部实现。
 */
final class WeightedRandomSupport {

    private WeightedRandomSupport() {
    }

    static Map<String, Double> effectivePool(Map<String, Double> source, List<String> hidden) {
        Map<String, Double> probability = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : source.entrySet()) {
            Double value = entry.getValue();
            if (value != null && value > 0d) {
                probability.put(entry.getKey(), value);
            }
        }
        if (hidden != null && !hidden.isEmpty()) {
            for (String key : hidden) {
                probability.remove(key);
            }
        }
        if (probability.isEmpty()) {
            throw new IllegalArgumentException("概率配置异常：所有概率都为 0 或被隐藏");
        }
        return probability;
    }

    static String pickByWheel(Map<String, Double> probability, Random random) {
        double sum = probability.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum <= 0d) {
            throw new IllegalArgumentException("概率配置异常：总概率为 0");
        }
        double target = random.nextDouble(sum);
        double acc = 0d;
        for (Map.Entry<String, Double> entry : probability.entrySet()) {
            acc += entry.getValue();
            if (target < acc) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("概率配置异常：未命中任何项");
    }

    /**
     * 整数配额 + 剩余轮盘，提升极小概率精度（10 万次采样约 0.00001）。
     */
    static String pickByQuota(Map<String, Double> probability, Random random, int sampleBase) {
        double total = probability.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0d) {
            throw new IllegalArgumentException("概率配置异常：总概率为 0");
        }

        Map<String, Double> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : probability.entrySet()) {
            normalized.put(entry.getKey(), entry.getValue() / total);
        }

        Map<String, Integer> quotaMap = new LinkedHashMap<>();
        Map<String, Double> remainMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : normalized.entrySet()) {
            double scaled = entry.getValue() * sampleBase;
            quotaMap.put(entry.getKey(), (int) scaled);
            remainMap.put(entry.getKey(), scaled - (int) scaled);
        }

        List<String> remainKeys = new ArrayList<>(remainMap.keySet());
        double[] remainCumulative = new double[remainKeys.size()];
        double remainSum = 0d;
        for (int i = 0; i < remainKeys.size(); i++) {
            remainSum += remainMap.get(remainKeys.get(i));
            remainCumulative[i] = remainSum;
        }

        int index = random.nextInt(sampleBase);
        int acc = 0;
        for (Map.Entry<String, Integer> entry : quotaMap.entrySet()) {
            acc += entry.getValue();
            if (index < acc) {
                return entry.getKey();
            }
        }

        if (remainSum <= 0d) {
            throw new IllegalStateException("概率配置异常：未命中任何项");
        }
        double rand = random.nextDouble() * remainSum;
        for (int i = 0; i < remainCumulative.length; i++) {
            if (rand < remainCumulative[i]) {
                return remainKeys.get(i);
            }
        }
        throw new IllegalStateException("概率配置异常：未命中任何项");
    }

    static String pickUniform(Map<String, Double> probability, Random random) {
        List<String> keys = new ArrayList<>(probability.keySet());
        return keys.get(random.nextInt(keys.size()));
    }

    static String pick(
            Map<String, Double> probability,
            Random random,
            WeightedRandomAlgorithm algorithm,
            int sampleBase) {
        return switch (algorithm) {
            case UNIFORM -> pickUniform(probability, random);
            case WHEEL -> pickByWheel(probability, random);
            case QUOTA -> pickByQuota(probability, random, sampleBase);
        };
    }
}
