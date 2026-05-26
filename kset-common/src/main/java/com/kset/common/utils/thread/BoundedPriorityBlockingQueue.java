package com.kset.common.utils.thread;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 有界优先阻塞队列。
 *
 * <p>继承 {@link PriorityBlockingQueue} 但限制容量，当队列达到容量时
 * {@link #offer(Object)} 返回 false，从而可与 {@link java.util.concurrent.ThreadPoolExecutor}
 * 配合实现 maximumPoolSize 的线程扩容语义。</p>
 *
 * <p>与标准无界 {@code PriorityBlockingQueue} 不同，本队列在满时拒绝入队，
 * 确保线程池在队列满载后仍可创建新线程至 maximumPoolSize。</p>
 */
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
