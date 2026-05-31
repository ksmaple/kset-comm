package com.kset.demo.order.infrastructure.persistence;

import com.kset.demo.order.domain.model.Order;
import com.kset.demo.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MybatisOrderRepository implements OrderRepository {

    private final OrderMapper orderMapper;

    public MybatisOrderRepository(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public Order findById(Long id) {
        return toDomain(orderMapper.selectById(id));
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        orderMapper.insert(entity);
        return toDomain(entity);
    }

    private Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        Order order = new Order();
        order.setId(entity.getId());
        order.setUserId(entity.getUserId());
        order.setProductName(entity.getProductName());
        order.setCreateTime(entity.getCreateTime());
        order.setUpdateTime(entity.getUpdateTime());
        order.setDeleted(entity.getDeleted());
        return order;
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setProductName(order.getProductName());
        entity.setCreateTime(order.getCreateTime());
        entity.setUpdateTime(order.getUpdateTime());
        entity.setDeleted(order.getDeleted());
        return entity;
    }
}
