# kset-demo 示例工程

两个独立示例，对应不同接入方式。各模块配置写在 **`src/main/resources/application.yaml`**；[env](env) 目录仅提供可复制的配置示例。

## 1. 单机项目 — `demo-standalone-service`

| 项 | 说明 |
|----|------|
| 依赖 | web + datasource + PostgreSQL + redis + monitor |
| 端口 | 18081 |
| 中间件 | PostgreSQL（`127.0.0.1:5432/kset_demo`）、Redis |
| 启动 | `mvn -pl kset-demo/demo-standalone-service spring-boot:run` |

- API：http://localhost:18081/api/users/1
- 文档：http://localhost:18081/doc.html

## 2. 微服务 Cloud — `demo-micro-service` / `demo-gateway`

| 模块 | 端口 | 说明 |
|------|------|------|
| `demo-micro-service` | 18082 | 用户与订单微服务示例，包含 Dubbo Provider/Consumer + Redis（Nacos + Sentinel） |
| `demo-gateway` | 8080 | 网关（starter-gateway + monitor） |

中间件：PostgreSQL、Redis、Nacos（`NACOS_ADDR` 默认 `127.0.0.1:8848`）。

```bash
mvn clean install
mvn -pl kset-demo/demo-micro-service spring-boot:run
mvn -pl kset-demo/demo-gateway spring-boot:run
```

Gateway 路由样例：[docs/nacos/demo-gateway-routes.json](../docs/nacos/demo-gateway-routes.json)。

切换数据库或叠加组件时，从 [env/README.md](env/README.md) 复制示例到对应 `application.yaml`。

详见 [docs/getting-started.md](../docs/getting-started.md)。
