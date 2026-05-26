package com.kset.gateway.spi;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway 鉴权 SPI
 */
public interface GatewayAuthProvider {

    /**
     * @return empty 表示放行；非 empty 表示拒绝并完成响应
     */
    Mono<Void> authenticate(ServerWebExchange exchange);
}
