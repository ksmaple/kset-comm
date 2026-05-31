# CAT 环境配置

本目录保存 CAT 部署必需文件。运行数据不写入仓库，统一挂载到宿主机 `/data`。

## 文件说明

| 文件 | 说明 |
|------|------|
| `docker-compose.yml` | CAT Server 和内置 MySQL 服务定义 |
| `Dockerfile` | 基于 Tomcat 构建 CAT Server 镜像 |
| `datasources.sh` | 容器启动时按环境变量生成 `datasources.xml`、`server.xml` |
| `client/client.xml` | CAT 客户端连接配置源 |
| `server/cat-home.war` | CAT Server 应用包 |
| `server/init_cat.sql` | 内置 MySQL 首次启动初始化脚本 |

## 启动

推荐在仓库根目录执行：

```powershell
.\env\script\up.ps1 -Build
```

Linux/macOS/Git Bash：

```bash
./env/script/up.sh --build
```

查看 CAT 日志：

```powershell
cd env
docker compose --env-file .env -f docker-compose.yml -f cat/docker-compose.yml logs -f cat
```

## 关键环境变量

| 变量 | 默认值 | 含义 |
|------|--------|------|
| `CAT_SERVER_IP` | `127.0.0.1` | 客户端访问 CAT Server 的宿主机地址 |
| `CAT_HTTP_PORT` | `8088` | CAT 控制台宿主机端口 |
| `CAT_TCP_PORT` | `2280` | CAT 客户端上报 TCP 端口 |
| `CAT_JOB_MACHINE` | `true` | 是否启用 CAT 任务节点 |
| `CAT_ALERT_MACHINE` | `true` | 是否启用 CAT 告警节点 |
| `CAT_MYSQL_PORT` | `3307` | 内置 MySQL 宿主机端口 |
| `CAT_MYSQL_DATABASE` | `cat` | CAT 数据库名 |
| `CAT_MYSQL_USER` | `root` | CAT 数据库用户 |
| `CAT_MYSQL_PASSWORD` | 空 | CAT 数据库密码 |

## 数据目录

| 内容 | 宿主机目录 |
|------|------------|
| CAT Server 运行数据 | `/data/appdatas/cat/` |
| CAT 客户端连接配置 | `/data/appdatas/cat/client.xml` |
| CAT 内置 MySQL 数据 | `/data/cat/mysql/` |

CAT 启动后，`/data/appdatas/cat/` 下应包含 `client.xml`、`datasources.xml`、`server.xml` 等服务端配置文件。
