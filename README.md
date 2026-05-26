# kset-comm

KSet Spring Boot 公共框架 — 统一版本管理、按能力拆分的 Starter、云服务规则定制层。

## 模块结构

```
kset-comm/
├── kset-spring-boot-parent/              # 版本 BOM（Boot 3.4.5 / SC 2024 / SCA 2023 / Dubbo 3.3）
├── kset-common/                          # 公共工具（异常、日志、TraceId、DateHelper、HTTP、线程池）
├── kset-cloud/                           # 云服务规范（kset.cloud.*、SPI、TraceContext）
├── kset-spring-boot-starter-web/         # Web + 统一异常 + TraceId
├── kset-spring-boot-starter-mysql/       # JDBC + MySQL + MyBatis-Plus + Flyway
├── kset-spring-boot-starter-redis/       # Spring Data Redis (Lettuce)
├── kset-spring-boot-starter-nacos/       # Nacos + Sentinel + 灰度 LoadBalancer
├── kset-spring-boot-starter-dubbo/       # Dubbo RPC + TraceId 透传 + 标签路由
└── kset-spring-boot-starter-gateway/     # Spring Cloud Gateway + 动态路由 + Sentinel
└── kset-demo/                            # 示例：standalone（单机）+ cloud（微服务）
```

## 包名与模块目录约定

Java 包根路径与 Maven 模块目录一一对应（`src/main/java` 下目录即包路径）：

| Maven 模块 | 包根路径 | 说明 |
|------------|----------|------|
| `kset-common` | `com.kset.common` | 公共工具、异常、日志、TraceId |
| `kset-cloud` | `com.kset.cloud` | 云服务规范、SPI、共享配置与 Nacos 命名约定 |
| `kset-spring-boot-starter-web` | `com.kset.web` | Web 自动配置与统一响应 |
| `kset-spring-boot-starter-mysql` | `com.kset.mysql` | MyBatis-Plus / Flyway |
| `kset-spring-boot-starter-redis` | `com.kset.redis` | Redis 模板与缓存 |
| `kset-spring-boot-starter-nacos` | `com.kset.nacos` | Nacos / Sentinel 自动配置 |
| `kset-spring-boot-starter-dubbo` | `com.kset.dubbo` | Dubbo 治理与路由 |
| `kset-spring-boot-starter-gateway` | `com.kset.gateway` | Gateway 过滤器与动态路由 |
| `kset-demo/*` | `com.kset.demo.*` | 示例应用 |

跨模块依赖时，Starter 实现类引用 `kset-cloud` 中的共享 API（如 `com.kset.cloud.spi.CloudRuleProvider`、`com.kset.cloud.nacos.NacosConfigConvention`）。

## 文档

| 文档 | 说明 |
|------|------|
| [docs/getting-started.md](docs/getting-started.md) | **单机 vs 微服务 Cloud** 依赖与配置 |
| [docs/kset-common-utils.md](docs/kset-common-utils.md) | `DateHelper`（java.time）、`KsetHttp`、线程池 |
| [kset-demo/README.md](kset-demo/README.md) | 两个示例工程启动说明 |
| [docs/openapi.md](docs/openapi.md) | API 文档（Knife4j / OpenAPI 3） |
| [docs/nacos/demo-gateway-routes.json](docs/nacos/demo-gateway-routes.json) | Gateway 路由样例 |

## Starter 能力说明

| Starter | KSet 定制能力 | 第三方默认行为 |
|---------|--------------|----------------|
| `kset-spring-boot-starter-web` | TraceId、全局异常、ApiResponse、OpLog AOP、可选 Knife4j/请求日志 | Spring MVC / Validation |
| `kset-spring-boot-starter-mysql` | 逻辑删除约定、createTime/updateTime 自动填充、Flyway 默认路径 | JDBC / MyBatis-Plus / Flyway |
| `kset-spring-boot-starter-redis` | JSON 序列化 RedisTemplate、Key 前缀、可选 Cache | Spring Data Redis |
| `kset-spring-boot-starter-nacos` | Nacos 命名约定、Sentinel 三类规则、灰度 LB | SCA Nacos / Sentinel |
| `kset-spring-boot-starter-dubbo` | Trace/灰度透传、标签路由、路由冷启动拉取 | Apache Dubbo |
| `kset-spring-boot-starter-gateway` | 动态路由 diff、TraceId/灰度、可选鉴权、Gateway Sentinel | Spring Cloud Gateway |

## 版本矩阵

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.4.5 |
| Java | 21 |
| Spring Cloud | 2024.0.0 |
| Spring Cloud Alibaba | 2023.0.3.2 |
| Apache Dubbo | 3.3.6 |
| MyBatis-Plus | 3.5.5 |
| Fastjson2 | 2.0.53 |
| Hutool | 5.8.34 |
| Guava | 33.4.0-jre |
| EasyExcel | 4.0.3 |
| OkHttp | 4.12.0 |
| Caffeine | 3.2.0 |
| JJWT | 0.12.6 |
| Apache POI | 5.3.0 |
| Apache Tika | 2.9.1 |
| BouncyCastle (jdk18on) | 1.78.1 |
| Protobuf Java | 3.25.8 |
| Commons Compress | 1.26.2 |

业务项目继承 `kset-spring-boot-parent` 后，可直接引用下列依赖（**无需写 version**）：

```xml
<!-- JSON：请用 fastjson2，勿用已停更的 fastjson 1.x -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
</dependency>

<!-- 工具库 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>

<!-- HTTP / 缓存 / Excel / JWT / 链路上下文 -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>transmittable-thread-local</artifactId>
</dependency>

<!-- 加密 / 编解码 -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk18on</artifactId>
</dependency>

<!-- 文件 / Excel / 文档解析 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-core</artifactId>
</dependency>
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
</dependency>
```

更多版本见 `kset-spring-boot-parent/pom.xml` 中 `dependencyManagement`。

**依赖冲突排查**（全 reactor 扫描 `omitted for conflict`）：

```bash
mvn dependency:tree -Dverbose | findstr "omitted for conflict"
```

当前 BOM 已统一 `protobuf-java`（MySQL Connector 3.25.1 vs Dubbo 3.25.8）、POI 全家桶与 BouncyCastle 传递版本。

| 项 | 要求 | 说明 |
|----|------|------|
| **JDK** | **21**（LTS） | `maven-enforcer-plugin` 构建时校验 `[21,22)`；IDE Project SDK 选 21 |
| **Maven** | **3.9+** | 构建时校验；避免使用会解析 `maven-compiler-plugin` 4.x beta 的异常环境 |
| **源码编码** | **UTF-8** | `project.build.sourceEncoding` / `.editorconfig` / `.gitattributes` 已统一 |
| **编译** | `--release 21` | 由 `kset-spring-boot-parent` 配置，含 `-parameters`（Spring / Dubbo 需要） |
| **JVM 默认编码** | UTF-8 | 仓库 `.mvn/jvm.config` 已设置 `-Dfile.encoding=UTF-8` |

**IDE 建议（IntelliJ / Cursor）：**

1. **Project Structure → Project SDK**：JDK **21**
2. **Settings → Editor → File Encodings**：Global / Project 均为 **UTF-8**
3. **Maven → Reload All Maven Projects**（修改 parent POM 后必做）
4. 终端若中文乱码，确认 `JAVA_TOOL_OPTIONS` 或系统区域设置未强制 GBK

**业务项目继承 parent 时无需再配 Java 版本与编码**，除非有特殊模块需求。

## 快速开始

完整说明见 **[docs/getting-started.md](docs/getting-started.md)**（单机 / 微服务 Cloud 分开展示）。

| 场景 | 示例工程 | Starter 组合 |
|------|----------|--------------|
| **单机** | `kset-demo/demo-standalone-service` | web + mysql + redis |
| **微服务 Cloud** | `demo-user-service` / `demo-order-service` / `demo-gateway` | web + mysql (+redis) + nacos + dubbo；网关单独 gateway |

```xml
<parent>
    <groupId>com.kset</groupId>
    <artifactId>kset-spring-boot-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>
```

**单机** — 仅业务进程，无 Nacos/Dubbo/Gateway：

```xml
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-spring-boot-starter-mysql</artifactId>
</dependency>
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-spring-boot-starter-redis</artifactId>
</dependency>
```

**微服务 Cloud** — 业务服务在此基础上增加：

```xml
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-spring-boot-starter-nacos</artifactId>
</dependency>
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-spring-boot-starter-dubbo</artifactId>
</dependency>
```

**Gateway**（独立进程，勿与 starter-web 同用）：

```xml
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-spring-boot-starter-gateway</artifactId>
</dependency>
```

## Nacos 规则配置约定

| 用途 | dataId 格式 | 示例 |
|------|------------|------|
| 应用主配置 | `{app}.yaml` | `order-service.yaml` |
| 公共配置 | `kset-common.yaml` | 团队共享默认值 |
| Sentinel 限流 | `{app}-flow-rules` | JSON 数组 |
| Sentinel 熔断 | `{app}-degrade-rules` | JSON 数组 |
| Dubbo 路由 | `{app}-route-rules` | JSON 对象 |
| Gateway 路由 | `{gateway-app}-gateway-routes` | JSON 数组 |
| Gateway 限流 | `{gateway-app}-gateway-flow-rules` | JSON 数组 |

### Sentinel 限流示例

```json
[
  {
    "resource": "/api/orders",
    "grade": 1,
    "count": 100,
    "strategy": 0,
    "controlBehavior": 0
  }
]
```

### Dubbo 路由示例

```json
{
  "conditions": [
    { "tag": "v2", "weight": 10 },
    { "tag": "stable", "weight": 90 }
  ]
}
```

### Gateway 动态路由示例

```json
[
  {
    "id": "order-service",
    "uri": "lb://order-service",
    "predicates": [
      { "name": "Path", "args": { "pattern": "/api/orders/**" } }
    ],
    "filters": [
      { "name": "StripPrefix", "args": { "parts": "1" } }
    ]
  }
]
```

## SPI 扩展

实现接口并注册为 Spring `@Component`：

| SPI | 包路径 | 用途 |
|-----|--------|------|
| `CloudRuleProvider` | `com.kset.cloud.spi` | 自定义 Sentinel / Dubbo / Gateway 规则变更处理 |
| `GrayTagResolver` | `com.kset.cloud.spi` | 自定义灰度标签解析（默认透传 Header） |
| `GatewayAuthProvider` | `com.kset.gateway.spi` | Gateway JWT / Token 鉴权 |

```java
import com.kset.cloud.spi.CloudRuleProvider;
import com.kset.cloud.spi.CloudRuleType;

@Component
public class OrderFlowRuleProvider implements CloudRuleProvider {
    @Override
    public CloudRuleType ruleType() {
        return CloudRuleType.SENTINEL_FLOW;
    }

    @Override
    public void onRuleChanged(String jsonContent) {
        // 额外处理逻辑
    }
}
```

## 全链路灰度

```
Client → Gateway (X-Gray-Tag) → LoadBalancer (metadata 匹配) → 微服务 (TraceIdFilter) → Dubbo (TraceFilter)
```

## kset-demo 本地启动

详见 [kset-demo/README.md](kset-demo/README.md)。

**单机**（MySQL + Redis）：

```bash
mvn -pl kset-demo/demo-standalone-service spring-boot:run
# http://localhost:8080/doc.html
```

**微服务 Cloud**（MySQL + Redis + Nacos）：

```bash
mvn -pl kset-demo/demo-user-service spring-boot:run
mvn -pl kset-demo/demo-order-service spring-boot:run
mvn -pl kset-demo/demo-gateway spring-boot:run
```

Gateway 路由：Nacos 配置 `demo-gateway-gateway-routes` 为 [docs/nacos/demo-gateway-routes.json](docs/nacos/demo-gateway-routes.json)。

## 构建

```bash
mvn clean install
```

## 后续迭代

- 完整 JWT/OAuth2 Gateway 鉴权
- Dubbo starter 依赖瘦身（纯 RPC 场景）
