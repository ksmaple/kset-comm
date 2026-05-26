package com.kset.common.utils.random;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 单次抽取事件，用于日志重放与审计。
 */
public record DrawEvent(
        String name,
        long seq,
        String key,
        long drawTimeMs,
        List<String> hiddenKeys,
        int configVersion,
        WeightedRandomAlgorithm algorithm,
        Long seed
) implements Serializable {

    public DrawEvent {
        hiddenKeys = hiddenKeys == null ? List.of() : List.copyOf(hiddenKeys);
    }

    public static DrawEvent of(
            String name,
            long seq,
            String key,
            long drawTimeMs,
            List<String> hiddenKeys,
            int configVersion,
            WeightedRandomAlgorithm algorithm,
            Long seed) {
        return new DrawEvent(name, seq, key, drawTimeMs, hiddenKeys, configVersion, algorithm, seed);
    }

    public List<String> hiddenKeysView() {
        return Collections.unmodifiableList(hiddenKeys);
    }
}
