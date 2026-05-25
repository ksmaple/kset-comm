package com.kset.cloud.gateway.filter;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.cloud.trace.TraceContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway TraceId 透传
 */
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {

    private final KsetCloudProperties properties;

    public TraceIdGatewayFilter(KsetCloudProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceHeader = properties.getGateway().getTraceHeader();
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(traceHeader);
        if (traceId == null || traceId.isBlank()) {
            traceId = TraceContext.generateTraceId();
        }
        String spanId = TraceContext.generateSpanId();
        final String finalTraceId = traceId;

        ServerHttpRequest mutated = request.mutate()
                .header(traceHeader, finalTraceId)
                .header(TraceContext.SPAN_ID_HEADER, spanId)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build())
                .contextWrite(ctx -> TraceContext.putReactorContext(ctx, finalTraceId, null))
                .doOnEach(signal -> {
                    if (signal.isOnComplete() || signal.isOnError()) {
                        TraceContext.clear();
                    }
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
