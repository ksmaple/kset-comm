package com.kset.web.filter;

import com.kset.common.logging.LogMaskingUtil;
import com.kset.common.monitor.Monitor;
import com.kset.common.monitor.facade.MonitorStatus;
import com.kset.common.monitor.facade.MonitorTypes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 可选 HTTP 请求/响应日志（敏感字段脱敏）。
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long cost = System.currentTimeMillis() - start;
            if (cost >= 500) {
                Monitor.logEvent(MonitorTypes.URL, "request-log", MonitorStatus.FAIL,
                        cost + "ms " + request.getMethod() + " " + request.getRequestURI());
            }
            if (log.isDebugEnabled()) {
                String body = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
                log.debug("HTTP {} {} status={} costMs={} traceId={} body={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        wrappedResponse.getStatus(),
                        cost,
                        Monitor.currentTraceId().orElse(null),
                        LogMaskingUtil.maskText(body));
            }
            wrappedResponse.copyBodyToResponse();
        }
    }
}
