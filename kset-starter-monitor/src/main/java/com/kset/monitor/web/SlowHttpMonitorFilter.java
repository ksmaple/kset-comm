package com.kset.monitor.web;

import com.kset.common.monitor.KsetMonitor;
import com.kset.monitor.config.KsetMonitorProperties;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * HTTP 慢请求监控（经门面 {@link KsetMonitor#recordSlowEvent} 上报）。
 */
public class SlowHttpMonitorFilter implements Filter {

    private final KsetMonitorProperties properties;

    public SlowHttpMonitorFilter(KsetMonitorProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!properties.getSlowLog().isHttpEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;
            long threshold = properties.getSlowLog().getHttpThresholdMs();
            if (cost >= threshold && request instanceof HttpServletRequest httpRequest) {
                KsetMonitor.recordSlowEvent("http",
                        cost,
                        httpRequest.getMethod() + " " + httpRequest.getRequestURI());
            }
        }
    }
}
