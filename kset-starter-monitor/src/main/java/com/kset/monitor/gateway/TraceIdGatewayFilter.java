package com.kset.monitor.gateway;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.common.monitor.GatewayTraceBinding;
import com.kset.common.monitor.KsetMonitor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Gateway TraceId 透传（经 {@link KsetMonitor} 门面）。
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
        String incomingTraceId = request.getHeaders().getFirst(traceHeader);
        GatewayTraceBinding binding = KsetMonitor.resolveGatewayTrace(incomingTraceId, traceHeader);

        ServerHttpRequest mutated = request.mutate()
                .header(binding.getTraceHeaderName(), binding.getTraceId())
                .header(binding.getSpanIdHeaderName(), binding.getSpanId())
                .build();

        final String finalTraceId = binding.getTraceId();
        return chain.filter(exchange.mutate().request(mutated).build())
                .contextWrite(ctx -> (Context) KsetMonitor.putReactorContext(ctx, finalTraceId, null))
                .doOnEach(signal -> {
                    if (signal.isOnComplete() || signal.isOnError()) {
                        KsetMonitor.clear();
                    }
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
