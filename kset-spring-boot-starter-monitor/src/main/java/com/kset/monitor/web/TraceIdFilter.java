package com.kset.monitor.web;

import com.kset.common.monitor.HttpTraceBinding;
import com.kset.common.monitor.KsetMonitor;
import com.kset.common.trace.TraceHeaders;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * TraceID 链路追踪过滤器（由 kset-spring-boot-starter-monitor 自动注册）。
 */
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String incomingTraceId = httpRequest.getHeader(TraceHeaders.TRACE_ID_HEADER);
        HttpTraceBinding binding = KsetMonitor.bindHttpIncoming(incomingTraceId);
        httpResponse.setHeader(binding.getTraceIdHeaderName(), binding.getTraceId());
        httpResponse.setHeader(binding.getSpanIdHeaderName(), binding.getSpanId());

        try {
            chain.doFilter(request, response);
        } finally {
            KsetMonitor.clear();
        }
    }
}
