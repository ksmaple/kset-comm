package com.kset.cloud.gateway.filter;

import com.kset.cloud.gateway.spi.GatewayAuthProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway 鉴权 Filter 插槽
 */
public class AuthGatewayFilter implements GlobalFilter, Ordered {

    private final ObjectProvider<GatewayAuthProvider> authProviders;

    public AuthGatewayFilter(ObjectProvider<GatewayAuthProvider> authProviders) {
        this.authProviders = authProviders;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        for (GatewayAuthProvider provider : authProviders) {
            Mono<Void> result = provider.authenticate(exchange);
            if (result != null) {
                return result;
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
