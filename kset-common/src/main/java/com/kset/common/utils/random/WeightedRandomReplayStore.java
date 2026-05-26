package com.kset.common.utils.random;

import java.util.List;

/**
 * 抽取事件日志持久化 SPI，用于审计与日志重放。
 */
public interface WeightedRandomReplayStore {

    long loadLatestSeq(String name);

    void appendDrawEvents(String name, List<DrawEvent> events);

    List<DrawEvent> loadDrawEvents(String name, long fromSeq, int limit);
}
