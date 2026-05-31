-- KSet PostgreSQL 本地初始化脚本。
-- 来源：PostgreSQL 官方 docker-entrypoint-initdb.d 初始化机制。
-- 含义：容器首次创建数据目录时执行；默认数据库由 POSTGRES_DB 创建，这里补充 demo 所需扩展和审计库。

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector;

-- 来源：PostgreSQL psql \gexec。
-- 含义：多数据源 demo 使用的审计库；只有不存在时才创建。
SELECT 'CREATE DATABASE kset_audit'
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'kset_audit')\gexec

\connect kset_audit

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector;
