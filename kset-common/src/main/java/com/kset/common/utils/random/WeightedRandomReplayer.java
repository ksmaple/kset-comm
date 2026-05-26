package com.kset.common.utils.random;

import java.util.Iterator;
import java.util.List;

/**
 * 只读重放会话：按 seq 顺序回放 {@link DrawEvent}，不推进 live 引擎状态。
 */
public class WeightedRandomReplayer implements Iterator<DrawEvent> {

    private final String name;
    private final Iterator<DrawEvent> iterator;
    private final WeightedRandomObserver observer;

    WeightedRandomReplayer(String name, List<DrawEvent> events, WeightedRandomObserver observer) {
        this.name = name;
        this.iterator = events.iterator();
        this.observer = observer;
    }

    public String getName() {
        return name;
    }

    public DrawEvent nextEvent() {
        if (!hasNext()) {
            throw new IllegalStateException("没有更多重放事件");
        }
        DrawEvent event = next();
        if (observer != null) {
            observer.onReplayStep(name, event);
        }
        return event;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public DrawEvent next() {
        return iterator.next();
    }
}
