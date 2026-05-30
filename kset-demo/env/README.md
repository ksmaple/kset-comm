# KSet Demo 环境配置

本目录集中放置 demo 可复用的本地环境配置，所有中间件地址默认指向 `127.0.0.1` 或 `localhost`。

配置原则：

- `application-global.yml` 是唯一全局覆盖配置，放应用名、端口、profile 等跨组件配置。
- 每个组件一个 `component-*.yml`，按需叠加。
- 组件文件只保留必选项；非必选项全部以注释形式保留来源和含义。
- 基础配置优先使用开源组件原生配置键；KSet 扩展配置只在组件覆盖文件中出现。

## 文件清单

| 文件 | 用途 |
|------|------|
| `application-global.yml` | 全局覆盖配置：端口、profile、应用名 |
| `component-mysql.yml` | MySQL / DataSource 配置 |
| `component-redis.yml` | Spring Redis 配置 |
| `component-knife4j.yml` | Knife4j / OpenAPI 配置 |
| `component-nacos.yml` | Nacos Discovery / Config 配置 |
| `component-dubbo.yml` | Dubbo 可选覆盖配置；基础项默认由 starter 自动派生 |
| `component-monitor.yml` | Monitor / CAT 后端覆盖配置 |
| `component-redisson.yml` | Redisson 分布式锁配置 |
| `component-cache.yml` | KSet Cache 可选覆盖配置 |
| `component-gateway.yml` | Gateway 可选覆盖配置 |
| `component-sentinel.yml` | Sentinel 可选覆盖配置 |
| `component-rocketmq.yml` | RocketMQ 配置 |
| `component-logging.yml` | 日志可选覆盖配置 |
| `component-web.yml` | KSet Web 可选覆盖配置 |
| `cat/client.xml` | CAT 客户端本地连接配置，默认连接 `127.0.0.1:2280` |

## 使用方式

单体应用基础组合：

```bash
mvn -pl kset-demo/demo-standalone-service spring-boot:run -Dspring-boot.run.arguments="--spring.config.import=optional:file:./kset-demo/env/application-global.yml,optional:file:./kset-demo/env/component-mysql.yml,optional:file:./kset-demo/env/component-redis.yml,optional:file:./kset-demo/env/component-knife4j.yml --spring.application.name=demo-standalone-service --server.port=8080"
```

微服务基础组合：

```bash
mvn -pl kset-demo/demo-user-service spring-boot:run -Dspring-boot.run.arguments="--spring.config.import=optional:file:./kset-demo/env/application-global.yml,optional:file:./kset-demo/env/component-mysql.yml,optional:file:./kset-demo/env/component-redis.yml,optional:file:./kset-demo/env/component-nacos.yml,optional:file:./kset-demo/env/component-knife4j.yml --spring.application.name=demo-user-service --server.port=8081"
```

启用 CAT 监控后端：

```bash
mvn -pl kset-demo/demo-standalone-service spring-boot:run -Dspring-boot.run.arguments="--spring.config.import=optional:file:./kset-demo/env/application-global.yml,optional:file:./kset-demo/env/component-mysql.yml,optional:file:./kset-demo/env/component-redis.yml,optional:file:./kset-demo/env/component-knife4j.yml,optional:file:./kset-demo/env/component-monitor.yml --spring.application.name=demo-standalone-service --server.port=8080"
```

启用 Redisson 分布式锁：

```bash
mvn -pl kset-demo/demo-standalone-service spring-boot:run -Dspring-boot.run.arguments="--spring.config.import=optional:file:./kset-demo/env/application-global.yml,optional:file:./kset-demo/env/component-mysql.yml,optional:file:./kset-demo/env/component-redis.yml,optional:file:./kset-demo/env/component-redisson.yml --spring.application.name=demo-standalone-service --server.port=8080"
```

CAT 客户端配置文件路径按 CAT 客户端默认查找规则加载。若本机 CAT Server 不在 `127.0.0.1`，先修改 `cat/client.xml` 中的 `ip`、`port` 和 `http-port`。
