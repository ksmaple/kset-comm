# KSet 公共框架（kset-framework）AI 协作配置

## 规则

- [kaka-project-rules](.claude/rules/kaka-project-rules.md)（含 R021–R029 书写约定）

## 技能

- 研发: `kset-framework-coder` → `.claude/skills/kset-framework-coder/`
- 修复: `kset-framework-fixer` → `.claude/skills/kset-framework-fixer/`
- 规范与写码编排: `kaka-coder-designer` → `.claude/skills/kaka-coder-designer/`

## 多环境

| 环境 | 技能路径 | 索引 |
|------|----------|------|
| Claude | `.claude/skills/` | `CLAUDE.md` |
| Codex | `.agents/skills/`（链接） | `AGENTS.md` |
| Cursor | `.cursor/skills/`（链接） | `.cursor/CLAUDE.md` |

运行 `scripts/setup-ai-env-links.ps1` 建立 Codex/Cursor 技能链接。

## 使用指引

**实现代码** → `kset-framework-coder`  
**修复** → `kset-framework-fixer`  
**新模块设计** → `kaka-coder-designer`（域 spec）→ 可选 `.claude/design/ddd|sql` → `kset-framework-coder`
