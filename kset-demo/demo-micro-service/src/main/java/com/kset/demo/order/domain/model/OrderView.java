package com.kset.demo.order.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "订单视图（含 Dubbo 查询的用户名）")
public record OrderView(
        @Schema(description = "订单 ID") Long id,
        @Schema(description = "商品名称") String productName,
        @Schema(description = "用户 ID") Long userId,
        @Schema(description = "用户名称") String userName) {
}
