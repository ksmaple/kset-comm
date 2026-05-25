package com.kset.core.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * TraceID 链路追踪过滤器
 *
 * <p>在请求入口处生成全局唯一 traceId，写入 SLF4J MDC 并随响应头返回客户端，
 * 实现请求全链路可追踪。traceId 从请求进入系统开始，贯穿所有日志输出，
 * 直至请求完成。
 *
 * <p>执行优先级最高（{@link Ordered#HIGHEST_PRECEDENCE}），确保后续所有
 * Filter 和 Interceptor 的日志均能携带 traceId。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String traceId = extractOrGenerateTraceId(httpRequest);
        String spanId = generateSpanId();
        MDC.put(TRACE_ID_KEY, traceId);
        MDC.put(SPAN_ID_KEY, spanId);
        httpResponse.setHeader(TRACE_ID_HEADER, traceId);
        httpResponse.setHeader(SPAN_ID_HEADER, spanId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_KEY);
            MDC.remove(SPAN_ID_KEY);
        }
    }

    /**
     * 从请求头提取 traceId，不存在则生成新的 32 位 UUID（去除横杠）
     */
    private String extractOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成 spanId：16 位十六进制字符串
     */
    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
