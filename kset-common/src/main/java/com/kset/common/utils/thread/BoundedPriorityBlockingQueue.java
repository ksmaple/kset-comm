package com.kset.common.utils.thread;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;


public class BoundedPriorityBlockingQueue<E> extends PriorityBlockingQueue<E> {
    private final int capacity;

    public BoundedPriorityBlockingQueue(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    public BoundedPriorityBlockingQueue(int capacity, java.util.Comparator<? super E> comparator) {
        super(capacity, comparator);
        this.capacity = capacity;
    }

    @Override
    public boolean offer(E e) {
        if (size() >= capacity) {
            return false;
        }
        return super.offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) {
        return offer(e);
    }

    @Override
    public boolean add(E e) {
        if (offer(e)) {
            return true;
        }
        throw new IllegalStateException("Queue full");
    }

    @Override
    public int remainingCapacity() {
        return capacity - size();
    }
}
