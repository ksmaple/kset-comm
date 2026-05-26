package com.kset.common.utils.random;

/**
 * 加权随机观测 SPI。
 */
public interface WeightedRandomObserver {

    default void onDraw(String name, String key, long totalDraws) {
    }

    default void onMetricsReport(String name, WeightedRandomMetrics metrics) {
    }

    default void onConfigChanged(String name, WeightedRandomConfig config) {
    }

    default void onReplayStep(String name, DrawEvent event) {
    }
}
