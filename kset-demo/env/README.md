# KSet Demo 环境配置示例

本目录仅提供**配置示例与模板**，运行时**不会**被 demo 服务自动加载。

各服务的实际配置已写入对应模块的 `src/main/resources/application.yaml`。需要调整中间件地址、切换数据库或叠加组件时，请从本目录复制相关片段到业务 `application.yaml`。

## 使用方式

1. 在 `component-*.yml` 中找到需要的配置项。
2. 复制到目标服务的 `application.yaml`（或 `application-{profile}.yaml`）。
3. 按需修改地址、账号、端口等环境相关值。

## 文件清单

| 文件 | 用途 |
|------|------|
| `component-mysql.yml` | MySQL 单数据源示例 |
| `component-pgsql.yml` | PostgreSQL 单数据源示例 |
| `component-sqlite.yml` | SQLite 单数据源示例 |
| `component-datasource-dynamic.yml` | dynamic-datasource 多数据源 |
| `component-redis.yml` | Spring Redis |
| `component-knife4j.yml` | Knife4j / OpenAPI |
| `component-nacos.yml` | Nacos Discovery / Config（含 `spring.config.import` 示例） |
| `component-dubbo.yml` | Dubbo 可选覆盖 |
| `component-monitor.yml` | CAT 监控后端 |
| `component-redisson.yml` | Redisson 分布式锁 |
| `component-cache.yml` | KSet Cache |
| `component-gateway.yml` | Gateway 可选覆盖 |
| `component-sentinel.yml` | Sentinel 可选覆盖 |
| `component-rocketmq.yml` | RocketMQ 5 Client |
| `component-logging.yml` | 日志可选覆盖 |
| `component-web.yml` | KSet Web 可选覆盖 |
| `cat/client.xml` | CAT 客户端连接示例 |

## 启动

```bash
mvn -pl kset-demo/demo-standalone-service spring-boot:run
mvn -pl kset-demo/demo-micro-service spring-boot:run
mvn -pl kset-demo/demo-gateway spring-boot:run
```

## CAT

- 各服务 `application.yaml` 已配置 `kset.monitor.backend=cat`。
- `mvn spring-boot:run` 时父 POM 注入 `-DCAT_HOME=.../kset-demo/env/cat`（读取本目录下的 `cat/client.xml` 示例）。
- IDE 运行可设置环境变量 `CAT_HOME=<仓库>/kset-demo/env/cat`，或执行 `.\env\script\sync.ps1` 同步到 `C:\data\appdatas\cat\`。

## Nacos

引入 Nacos Config 的微服务须在 `application.yaml` 中包含：

```yaml
spring:
  config:
    import:
      - optional:nacos:kset-common.yaml
      - optional:nacos:${spring.application.name}.yaml
```

示例见 `demo-micro-service` 的 `application.yaml` 与 `component-nacos.yml`。
