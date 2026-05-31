package com.kset.demo.order.domain.repository;

import com.kset.demo.order.domain.model.Order;

public interface OrderRepository {

    Order findById(Long id);

    Order save(Order order);
}
