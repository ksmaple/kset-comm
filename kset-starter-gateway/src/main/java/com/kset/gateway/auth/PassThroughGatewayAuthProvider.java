package com.kset.gateway.auth;

import com.kset.gateway.spi.GatewayAuthProvider;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 开发环境默认放行鉴权。
 */
public class PassThroughGatewayAuthProvider implements GatewayAuthProvider {

    @Override
    public Mono<Void> authenticate(ServerWebExchange exchange) {
        return Mono.empty();
    }
}
