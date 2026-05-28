package com.kset.gateway.filter;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.cloud.spi.GrayTagResolver;
import com.kset.common.monitor.KsetMonitor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Gateway 灰度标签注入（经 {@link KsetMonitor} 门面）。
 */
public class GrayTagGatewayFilter implements GlobalFilter, Ordered {

    private final KsetCloudProperties properties;
    private final GrayTagResolver grayTagResolver;

    public GrayTagGatewayFilter(KsetCloudProperties properties, GrayTagResolver grayTagResolver) {
        this.properties = properties;
        this.grayTagResolver = grayTagResolver;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String grayHeader = properties.getGateway().getGrayHeader();
        String headerValue = exchange.getRequest().getHeaders().getFirst(grayHeader);
        String grayTag = grayTagResolver.resolve(headerValue);

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(grayHeader, grayTag)
                .build();

        KsetMonitor.setGrayTag(grayTag);
        return chain.filter(exchange.mutate().request(mutated).build())
                .contextWrite(ctx -> (Context) KsetMonitor.putReactorContext(ctx, null, grayTag));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
