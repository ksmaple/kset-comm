package com.kset.demo.order.application;

import com.kset.demo.api.UserQueryService;
import com.kset.demo.order.domain.model.Order;
import com.kset.demo.order.domain.model.OrderView;
import com.kset.demo.order.domain.repository.OrderCacheRepository;
import com.kset.demo.order.domain.repository.OrderRepository;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OrderApplicationService {

    private static final Duration ORDER_CACHE_TTL = Duration.ofMinutes(10);

    private final OrderRepository orderRepository;
    private final OrderCacheRepository orderCacheRepository;

    @DubboReference
    private UserQueryService userQueryService;

    public OrderApplicationService(OrderRepository orderRepository, OrderCacheRepository orderCacheRepository) {
        this.orderRepository = orderRepository;
        this.orderCacheRepository = orderCacheRepository;
    }

    public OrderView getOrder(Long id) {
        OrderView cached = orderCacheRepository.findViewById(id);
        if (cached != null) {
            return cached;
        }
        Order order = orderRepository.findById(id);
        if (order == null) {
            return null;
        }
        String userName = userQueryService.getUserName(order.getUserId());
        OrderView view = new OrderView(order.getId(), order.getProductName(), order.getUserId(), userName);
        orderCacheRepository.saveView(id, view, ORDER_CACHE_TTL);
        return view;
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
}
