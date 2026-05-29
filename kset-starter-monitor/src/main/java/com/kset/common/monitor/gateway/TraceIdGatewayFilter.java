package com.kset.common.monitor.gateway;

import com.kset.cloud.config.KsetCloudProperties;
import com.kset.common.monitor.GatewayTraceBinding;
import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.TraceSnapshot;
import com.kset.common.monitor.facade.MonitorStatus;
import com.kset.common.monitor.facade.MonitorTransaction;
import com.kset.common.monitor.facade.MonitorTypes;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Gateway TraceId 透传（经 {@link Monitor} 门面）。
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
        TraceSnapshot previous = Monitor.capture();
        String incomingTraceId = request.getHeaders().getFirst(traceHeader);
        GatewayTraceBinding binding = Monitor.resolveGatewayTrace(incomingTraceId, traceHeader);
        Monitor.setTraceId(binding.getTraceId());
        Monitor.setSpanId(binding.getSpanId());

        ServerHttpRequest mutated = request.mutate()
                .header(binding.getTraceHeaderName(), binding.getTraceId())
                .header(binding.getSpanIdHeaderName(), binding.getSpanId())
                .build();

        final String finalTraceId = binding.getTraceId();
        final String txName = request.getMethod() + " " + request.getURI().getRawPath();
        final MonitorTransaction tx = Monitor.newTransaction(MonitorTypes.URL, txName);
        tx.addData("component", "gateway");
        tx.addData("method", request.getMethod().name());
        tx.addData("uri", request.getURI().getRawPath());
        TraceSnapshot gatewaySnapshot = Monitor.capture();
        return chain.filter(exchange.mutate().request(mutated).build())
                .contextWrite(ctx -> (Context) Monitor.putReactorContext(ctx, finalTraceId, null))
                .doOnSuccess(unused -> {
                    tx.addData("status", String.valueOf(exchange.getResponse().getRawStatusCode()));
                    tx.setStatus(MonitorStatus.SUCCESS);
                })
                .doOnError(error -> {
                    tx.setStatus(error);
                    tx.addData("errorType", error.getClass().getSimpleName());
                    Monitor.logError(error, txName);
                })
                .doFinally(signalType -> {
                    Monitor.restore(gatewaySnapshot);
                    tx.addData("signal", signalType.name());
                    if (signalType == reactor.core.publisher.SignalType.CANCEL) {
                        tx.setStatus(MonitorStatus.FAIL);
                    }
                    tx.close();
                    Monitor.restore(previous);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
