package com.kset.common.utils.random;

import java.util.Map;
import java.util.Optional;

/**
 * 加权随机配置与计数持久化 SPI（业务自行实现 Redis/MySQL 等）。
 */
public interface WeightedRandomPersistence {

    Optional<WeightedRandomConfig> loadConfig(String name);

    void saveConfig(String name, WeightedRandomConfig config);

    Map<String, Long> loadCounters(String name);

    void saveCounters(String name, Map<String, Long> counters);
}
