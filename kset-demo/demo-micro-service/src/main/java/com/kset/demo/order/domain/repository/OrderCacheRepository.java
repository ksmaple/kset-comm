package com.kset.demo.order.domain.repository;

import com.kset.demo.order.domain.model.OrderView;

import java.time.Duration;

public interface OrderCacheRepository {

    OrderView findViewById(Long id);

    void saveView(Long id, OrderView view, Duration ttl);
}
