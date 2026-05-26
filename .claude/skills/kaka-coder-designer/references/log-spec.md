# Log 域规范

L001: traceId 与 API X-Trace-Id 一致  
L002: 结构化 JSON 日志，含 level、traceId、logger、message、timestamp  
L003: HTTP 慢接口阈值大于 500ms 记 WARN，含 URL、Method、参数摘要  
L004: SQL 慢查询阈值大于 200ms 记 WARN，含 SQL、参数、返回行数  
L005: 外部 HTTP 慢调用阈值大于 1000ms 记 WARN  
L006: MQ 消费慢处理阈值大于 5000ms 记 WARN  
L007: 慢日志 WARN 接入告警，1 分钟内同类型超 10 条触发  
L008: 敏感字段脱敏：手机号中间四位、身份证中间位、邮箱用户名首字符加掩码  
L009: 禁止日志输出密码、token、密钥明文  
L010: 日志字段名与 API/DDD 语义对齐  
L011: 业务类使用 Lombok `@Slf4j` 声明 `log`，禁止手写 `LoggerFactory` 与 `@Log4j2` / `@Log`  
L012: 结构化字段使用 `StructLog`（类内 `private static final StructLog LOG = StructLog.of(X.class)`），禁止每次调用传入 `Logger`；`LogUtil` 静态方法仅框架内部或无法持有字段时使用；禁止字符串拼接未脱敏 PII  
L013: 统一 Logback 由 `kset-common` 提供 `classpath:kset-logback-spring.xml`，经 `KsetLoggingEnvironmentPostProcessor` 自动启用；接入方禁止重复维护 logback，覆盖用 `logging.config` 或 `kset.logging.auto-config=false`
