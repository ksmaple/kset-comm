# kset-common 工具类

`kset-common` 模块提供与业务无关的通用能力，包根路径为 `com.kset.common`。以下三类工具由 bobo-common-utils 迁移而来，**日期时间统一使用 JDK `java.time`**（不依赖 Joda-Time）。

## DateHelper（`com.kset.common.utils.date`）

链式日期时间 API，内部为 `ZonedDateTime`。

```java
import com.kset.common.utils.date.DateHelper;
import java.time.ZoneId;

// 格式化
String s = DateHelper.build()
        .withDateDef("2024-06-07 14:05:30")
        .toyyyyMMddHHmmss();

// 时区
DateHelper.buildCN();                    // GMT+8
DateHelper.buildSAU();                   // GMT+3
DateHelper.build(ZoneId.of("UTC"));

// 与 java.time 互转
DateHelper.build().toLocalDateTime();
DateHelper.build().toZonedDateTime();

// 区间 [start, end) 左闭右开
boolean ok = DateHelper.build().withDateDef("2024-01-01 12:00:00")
        .isRange(start, end);

// 自然月 / ISO 周 / 年 / 季度边界
DateHelper.DatePeriod month = DateHelper.thisMonthRange();
DateHelper.DatePeriod week = DateHelper.build().weekRangeExclusive();
```

| 迁移说明 | 原 Joda API | 现 API |
|----------|-------------|--------|
| 时区构造 | `build(DateTimeZone)` | `build(ZoneId)` / `build(TimeZone)` |
| 星期参数 | ISO 1=周一 … 7=周日 | 不变 |

常用模式常量：`PATTERN_DEF`、`CN_GMT`、`SAU_GMT` 等见类定义。

## KsetHttp（`com.kset.common.utils.http`）

基于 OkHttp 的 HTTP 客户端封装（原 `DDKJHttp`）。

```java
import com.kset.common.utils.http.KsetHttp;

String body = KsetHttp.get("https://example.com/api")
        .header("X-Token", token)
        .executeString();
```

配套：`HttpConvertUtils`、`HttpLogInterceptor`（SLF4J）、`RetryInterceptor`。

## 线程池（`com.kset.common.utils.thread`）

按业务名隔离、支持指标与 MDC 链路传递（原 `DDKJThreadPool*`）。

```java
import com.kset.common.utils.thread.KsetThreadPoolFactory;
import com.kset.common.utils.thread.MdcThreadPoolTraceAdapter;

KsetThreadPoolFactory factory = KsetThreadPoolFactory.getInstance();
factory.setGlobalTraceContextAdapter(new MdcThreadPoolTraceAdapter());
factory.register("order-payment", KsetThreadPoolFactory.PoolConfig.ioConfig());
factory.execute("order-payment", () -> callExternalApi());
```

| 类 | 说明 |
|----|------|
| `KsetThreadPoolFactory` | 推荐入口，按 biz 名懒创建池 |
| `KsetThreadPoolExecutor` | 底层实现，可 Builder 单独使用 |
| `ThreadPoolMetrics` / `ThreadPoolReporter` | 指标与上报 |
| `ThreadPoolTraceAdapter` / `MdcThreadPoolTraceAdapter` | TraceId 跨线程传递 |

## 随机（`com.kset.common.utils.random`）

### 基础 API（兼容旧版）

| 类 | 说明 |
|----|------|
| `RandomUtils` | `ThreadLocalRandom` 封装 |
| `RandomBox` | 加权轮盘，支持 `hidden(key)` |
| `SeededRandomBox` | 固定种子 + 整数配额（极小概率） |

### 推荐：Engine + Registry

| 类 | 说明 |
|----|------|
| `WeightedRandomEngine` | 单池引擎：可选加权、指标、journal、种子重放 |
| `KsetRandomRegistry` | 多业务注册中心（对标 `KsetThreadPoolFactory`） |
| `WeightedRandomMetrics` | 抽取分布快照（`toJson()`） |
| `DrawEvent` / `WeightedRandomReplayer` | 日志重放（审计/纠纷） |

```java
import com.kset.common.utils.random.*;

// 单引擎
WeightedRandomEngine engine = WeightedRandomEngine.builder()
        .name("lottery")
        .weights(Map.of("common", 0.9d, "rare", 0.1d))
        .metricsEnabled(true)
        .journalEnabled(true)
        .replayEnabled(true)
        .seed(42L)
        .replayStore(myReplayStore)
        .persistence(myPersistence)
        .build();

String prize = engine.draw("sold-out");
WeightedRandomMetrics metrics = engine.getMetrics();
engine.flush();

// 种子预览（需 replayEnabled + seed）
List<String> nextFive = engine.previewSequence(5);

// 多业务
KsetRandomRegistry reg = KsetRandomRegistry.getInstance();
reg.setGlobalPersistence(persistence);
reg.setGlobalReplayStore(replayStore);
reg.register("gacha", WeightedRandomConfig.builder().weights(weights).seed(1L).build());
reg.draw("gacha");
WeightedRandomReplayer replayer = reg.replay("gacha", 1000L);
```

### SPI（业务自行实现持久化）

| SPI | 用途 |
|-----|------|
| `WeightedRandomPersistence` | 配置与计数：`loadConfig` / `saveCounters` |
| `WeightedRandomReplayStore` | 抽取日志：`appendDrawEvents` / `loadDrawEvents` |
| `WeightedRandomObserver` | 观测：`onDraw` / `onMetricsReport` / `onReplayStep` |

**flush 策略**：`draw()` 热路径不写库；journal 缓冲满或 `flush()` 时批量刷盘；JVM 退出前建议 `KsetRandomRegistry.getInstance().flushAll()`。

**重放模式**：

- **种子重放**：`replayEnabled=true` + 固定 `seed` → `previewSequence(n)` / `replayFromSeed(n)`
- **日志重放**：`journalEnabled=true` + `ReplayStore` → `registry.replay(name, fromSeq)`（只读，不污染 live 计数）

单测可参考 `InMemoryRandomStorage`（test 包）。

## 签名（`com.kset.common.utils.sign`）

OpenAPI 常见规则：参数按 key 字典序拼接为 `secret + key1value1key2value2... + secret`，再做 SHA-1 或 MD5（与 bobo `BoBoSignUtil` 兼容）。

```java
import com.kset.common.utils.sign.KsetSignUtil;

KsetSignUtil signer = KsetSignUtil.of("app-secret");
Map<String, String> params = new LinkedHashMap<>();
params.put("appId", "10001");
params.put("timestamp", "1710000000");

String sign = signer.signSha1(params);   // 或 signMd5 / sign()
params.put("sign", sign);
boolean ok = signer.verifySha1(params);  // 或 checkSign()
```

## 依赖

业务工程通过任意 `kset-spring-boot-starter-*` 间接依赖 `kset-common`，也可直接声明：

```xml
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-common</artifactId>
</dependency>
```

`kset-spring-boot-parent` BOM 已管理 OkHttp、Guava、commons-lang3 等版本，**无需** 再引入 `joda-time`。
