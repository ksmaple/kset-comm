# KSet Cache 多级缓存

`kset-starter-cache` 提供统一缓存门面、KSet 自定义注解、L1 Caffeine、本地 single-flight、指标统计与 L2 SPI。它不依赖 `kset-starter-redis`；需要 Redis 二级缓存时，额外引入 `kset-starter-redis`，Redis 模块会自动注册 L2 适配器。

## 依赖

仅使用本地一级缓存：

```xml
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-starter-cache</artifactId>
</dependency>
```

使用 L1 + Redis L2：

```xml
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.kset</groupId>
    <artifactId>kset-starter-redis</artifactId>
</dependency>
```

## 配置

只用 L1 时要把默认层级改为 `L1`，否则声明 L2 但没有 L2 store 会启动失败。

```yaml
kset:
  cache:
    enabled: true
    default-layers: L1
    cache-null: true
    null-ttl: 1m
    single-flight-enabled: true
    l1:
      enabled: true
      default-ttl: 5m
      maximum-size: 10000
```

L1 + L2：

```yaml
kset:
  cache:
    default-layers: L1,L2
    l1:
      default-ttl: 5m
      maximum-size: 10000
    l2:
      required: true
      default-ttl: 30m
```

## 注解用法

```java
@KsetCacheable(cacheName = "user", key = "'user:id:' + #id", layers = {L1, L2})
public UserDTO getById(Long id) {
    return queryDb(id);
}

@KsetCachePut(cacheName = "user", key = "'user:id:' + #result.id", layers = {L1, L2})
public UserDTO save(UserDTO user) {
    return saveDb(user);
}

@KsetCaching(evict = {
        @KsetCacheEvict(cacheName = "user", key = "'user:id:' + #id", layers = {L1, L2}),
        @KsetCacheEvict(cacheName = "userByPhone", key = "'user:phone:' + #phone", layers = {L1, L2})
})
public void deleteUser(Long id, String phone) {
    deleteDb(id);
}
```

规则：

- 读顺序固定为 L1 -> L2 -> 方法加载。
- L2 命中后自动回填 L1。
- 方法加载成功后写入声明的所有层级。
- 默认缓存 `null`，使用 `kset.cache.null-ttl`。
- key 使用 SpEL，支持 `#id`、`#result`、`#root.methodName`。

## 编程式 API

```java
KsetCacheSpec spec = KsetCacheSpec.builder("user", "user:id:" + id)
        .layers(L1, L2)
        .ttl(Duration.ofMinutes(10))
        .valueType(UserDTO.class)
        .build();

UserDTO user = KsetCache.getOrLoad(spec, UserDTO.class, () -> queryDb(id));
KsetCache.put(spec, user);
KsetCache.evict(spec);
```

也可注入 `KsetCacheFacade`：

```java
public class UserCache {
    private final KsetCacheFacade cacheFacade;

    public UserCache(KsetCacheFacade cacheFacade) {
        this.cacheFacade = cacheFacade;
    }

    public Optional<UserDTO> get(Long id) {
        KsetCacheSpec spec = KsetCacheSpec.builder("user", "user:id:" + id)
                .layers(L1)
                .valueType(UserDTO.class)
                .build();
        return cacheFacade.getValue(spec, UserDTO.class);
    }
}
```

## 指标与监控

本地快照：

```java
KsetCacheMetrics metrics = KsetCache.metrics();
long hits = metrics.hits();
long misses = metrics.misses();
long errors = metrics.errors();
```

字段：

| 字段 | 含义 |
|------|------|
| `l1Hits` | L1 命中次数 |
| `l2Hits` | L2 命中次数 |
| `misses` | 全部声明层未命中次数 |
| `loads` | 未命中后执行 loader / 原方法次数 |
| `puts` | 逻辑写缓存次数 |
| `evicts` | 缓存删除次数 |
| `errors` | 缓存 get / put / evict / load 异常次数 |

`Monitor` 指标：

| 指标 | 含义 |
|------|------|
| `kset.cache.l1.hit` | L1 命中计数 |
| `kset.cache.l2.hit` | L2 命中计数 |
| `kset.cache.miss` | 未命中计数 |
| `kset.cache.load` | 加载计数 |
| `kset.cache.put` | 写入计数 |
| `kset.cache.evict` | 删除计数 |
| `kset.cache.error` | 异常计数 |

缓存操作也会创建 `Monitor` Transaction：`Cache/get.L1`、`Cache/get.L2`、`Cache/put.*`、`Cache/evict.*`。监控异常只记录日志和错误指标，不影响业务流程。
