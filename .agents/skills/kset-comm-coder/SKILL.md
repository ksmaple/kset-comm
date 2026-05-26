---
name: kset-comm-coder
description: "KSet Spring Boot 公共框架（框架库）研发，proj=kset-comm。触发：实现功能、改业务代码、按 project-spec 与 coder-designer 编码。仅改本仓。"
---

# kset-comm-coder

> [project-spec.md](references/project-spec.md) + `.claude/skills/kaka-coder-designer/references/`（init 已复制）。

| 项 | 值 |
|----|-----|
| proj | `kset-comm` |
| 类型 | **框架库（Spring Boot Starter 聚合仓）** |

## 触发条件

**启用**：在本仓实现功能、修改业务源码、按 project-spec 与 coder-designer 域 spec 编码。

**不启用**：纯规范设计无写码 → `kaka-coder-designer`；编译/测试失败修错 → `kset-comm-fixer`；git commit/push → `kaka-util-git-commit`；平台 init → `kaka-utils-project-init`。

## 核心规则

R001: 先读 project-spec.md，再按需读 ../kaka-coder-designer/references/{domain}-spec.md
R002: 规范冲突以 **project-spec** 为准，其次 coder-designer references
R003: **实现业务代码时只用本技能**；规范来自 project-spec + kaka-coder-designer
R004: 新模块约定写入 project-spec §3；实现对照 coder-designer 对应域
R005: 禁止引用**平台仓库外**绝对路径；规范在 `.claude/skills/kaka-coder-designer/`
R006: 禁止替代 coder-designer 做未约定的架构决策
R007: 编译/验证：`mvn -q -DskipTests compile`

## 工作流

```
Step 0: 确认任务为写码（否则路由到其他技能）
Step 1: 读 project-spec.md；按需读 coder-designer 域 *-spec.md
Step 2: 列变更范围（遵循项目规则 R007，可豁免时声明）
Step 3: 实现代码，遵守 project-spec 与域 spec
Step 4: 执行 mvn -q -DskipTests compile；失败则交 kset-comm-fixer
Step 5: 用户明确要求时交 kaka-util-git-commit
```

## 参考文件

| 文件 | 说明 |
|------|------|
| [project-spec.md](references/project-spec.md) | 项目差异与约定 |
| [coordination.md](../kaka-coder-designer/references/coordination.md) | 域协作 · 写码编排（codegen/compile/test） |

## Collaboration

| 场景 | 技能 |
|------|------|
| 规范设计 | `kaka-coder-designer` |
| 修错 | `kset-comm-fixer` |
| Git 提交 | `kaka-util-git-commit`（用户要求时） |
