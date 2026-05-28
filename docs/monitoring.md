# KSet 全链路监控与门面层

## 依赖

```xml
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-starter-monitor</artifactId>
</dependency>
```

引入后 `kset.monitor.enabled=true`（默认）自动装配链路透传；门面由 `KsetMonitorFacadeAutoConfiguration` 注册。

## 统一门面 `KsetMonitor`

| API | 用途 |
|-----|------|
| `KsetMonitor.currentTraceId()` | 业务代码获取当前 traceId |
| `KsetMonitor.bindHttpIncoming(headerValue)` | Servlet 入站（Filter 内部） |
| `KsetMonitor.bindDubboConsumer/Provider(...)` | Dubbo Filter |
| `KsetMonitor.resolveGatewayTrace(...)` | Gateway Filter |
| `KsetMonitor.capture()` / `restore()` / `openScope()` | 异步/线程池传递 |
| `KsetMonitor.recordSlowEvent(type, costMs, message)` | 慢调用上报 |

**禁止**在业务或新组件中直接使用 `MDC.put("traceId", ...)` 或已废弃的 `TraceContext`（兼容层仍可用）。

## 无感知能力矩阵

| 能力 | 条件 | 说明 |
|------|------|------|
| Servlet TraceId | monitor + Servlet | `TraceIdFilter`，响应头 `X-Trace-Id` |
| Servlet 灰度 MDC | monitor + Servlet | `GrayTagServletFilter` |
| Dubbo 透传 | monitor + Dubbo | `DubboTraceFilter` |
| Gateway TraceId | monitor + Gateway | `TraceIdGatewayFilter` |
| Gateway 灰度 | starter-gateway | `GrayTagGatewayFilter` |
| 日志 traceId | KSet Logback | `%X{traceId}` |
| 线程池 MDC | monitor | `KsetThreadPoolFactory` + `MdcThreadPoolTraceAdapter` |
| `@Async` 传播 | monitor，`kset.monitor.async.enabled=true` | `ThreadPoolTaskExecutorCustomizer` |
| HTTP 慢请求 WARN | monitor，`kset.monitor.slow-log.http-enabled=true` | 默认阈值 500ms |
| SQL 慢查询 WARN | starter-mysql，`kset.mysql.slow-sql.enabled=true` | 默认阈值 200ms，经门面 `recordSlowEvent` |
| ApiResponse.traceId | starter-web，`kset.web.response.trace-id-enabled=true` | `TraceIdResponseBodyAdvice` |

## 需手动埋点

| 能力 | 做法 |
|------|------|
| 操作审计 | starter-web：方法上 `com.kset.web.annotation.OpLog` |
| 结构化字段 | `StructLog.of(X.class)` |
| 线程池指标 | 实现 `ThreadPoolReporter` 并 `setGlobalReporter` |
| 自定义灰度 | 实现 `GrayTagResolver` |
| 自建线程池 | `KsetMonitor.capture()` + `openScope()` |
| 定时任务 | 自行生成/绑定 traceId |

## 配置示例

```yaml
kset:
  monitor:
    enabled: true
    servlet:
      trace-enabled: true
      gray-tag-enabled: true
    dubbo:
      enabled: true
    gateway:
      trace-enabled: true
    thread-pool:
      trace-propagation-enabled: true
    async:
      enabled: true
    slow-log:
      http-enabled: true
      http-threshold-ms: 500
  web:
    response:
      trace-id-enabled: true
  cloud:
    dubbo:
      trace-propagation-enabled: true
  mysql:
    slow-sql:
      enabled: true
      threshold-ms: 200
```

## 扩展自定义门面

```java
@Bean
@ConditionalOnMissingBean
public KsetMonitorFacade customMonitorFacade() {
    return new MyOtelMonitorFacade();
}
```

Spring 启动时 `KsetMonitor.install(facade)` 会替换默认占位实现，注册 `com.kset.monitor.internal.MdcMonitorFacade`。
