package com.kset.demo.order.interfaces.web;

import com.kset.demo.order.application.OrderApplicationService;
import com.kset.demo.order.domain.model.Order;
import com.kset.demo.order.domain.model.OrderView;
import com.kset.web.annotation.OpLog;
import com.kset.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单", description = "订单查询与创建（含 Dubbo、Redis 缓存）")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @Operation(summary = "按 ID 查询订单（含用户名）")
    @GetMapping("/{id}")
    public ApiResponse<OrderView> get(@PathVariable Long id) {
        OrderView view = orderApplicationService.getOrder(id);
        if (view == null) {
            return ApiResponse.fail("order not found");
        }
        return ApiResponse.success(view);
    }

    @Operation(summary = "创建订单")
    @PostMapping
    @OpLog(type = "CREATE", target = "order", recordParams = true)
    public ApiResponse<Order> create(@RequestBody CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        order.setProductName(request.productName());
        return ApiResponse.success(orderApplicationService.createOrder(order));
    }

    public record CreateOrderRequest(
            @Schema(description = "用户 ID") Long userId,
            @Schema(description = "商品名称") String productName) {
    }
}
