# Cloud Social Platform 云上社交平台

图文视频分享社交平台 —— 支持内容上传、瀑布流 Feed、点赞、分享链接与点赞统计，已部署于阿里云。

> 🌐 线上地址：**http://121.40.108.157:8081/**
> 课程作业项目，UI/UX 体验优先，点赞数作为成绩依据。

## ✨ 功能

- 👤 用户注册/登录（JWT 认证）
- 📷 图文/视频上传（类型校验、大小限制、FFmpeg 转码缩略图）
- 🌊 瀑布流 Feed（无限滚动、关键词搜索）
- ❤️ 点赞系统（Redis 实时计数 + 防刷限流 + 乐观 UI）
- 🔗 分享短链（Base62 8 位短码 + 公开落地页 + 访问追踪）
- 📱 响应式设计 + 深色模式 + 骨架屏

## 技术栈

- **后端**：Kotlin 1.9 + Spring Boot 3.5 + Spring Data JPA + MySQL 8.0 + Redis
- **前端**：React 19 + TypeScript + Vite 8 + **shadcn/ui 风格** + Tailwind CSS v4
- **视频处理**：FFmpeg（转码 + 缩略图）
- **部署**：阿里云 ECS（Ubuntu 22.04）+ Nginx + systemd

## 项目结构

```
cloud-social-platform/
├── AGENTS.md       # 项目总纲（AI 必读）
├── WORKFLOW.md     # 7 步开发流程与验收标准
├── docs/           # 架构/数据库/API/部署/运维文档
├── backend/        # Kotlin + Spring Boot 后端
├── frontend/       # React + Vite 前端
├── skills/         # UI/UX skill（shadcn/ui 定制规范）
└── scripts/        # 点赞统计等辅助脚本
```

## 文档

| 文档 | 说明 |
|---|---|
| [架构设计](docs/architecture.md) | 模块化单体 + 架构图 + 流程设计 |
| [数据库设计](docs/database-design.md) | ER 图 + 5 张表 + 索引 + 缓存设计 |
| [API 文档](docs/api.md) | 全部接口 + 错误码 |
| [部署文档](docs/deployment.md) | 服务器部署全流程 |
| [运维手册](docs/operations.md) | 巡检/备份/排障 |

## 快速开始

```bash
# 后端（端口 9090）
cd backend && ./gradlew bootRun

# 前端（开发模式）
cd frontend && npm install && npm run dev
```

环境变量：`DB_HOST`、`DB_PORT`、`DB_USER`、`DB_PASSWORD`、`DB_NAME`、`REDIS_HOST`、`REDIS_PORT`、`UPLOAD_DIR`。

## 版本

| 版本 | 内容 | Tag |
|---|---|---|
| v1.0.0 | 上传 + 展示 | 待打 |
| v1.1.0 | 点赞系统 | 待打 |
| v1.2.0 | 分享链接 | 待打 |
| v1.3.0 | UI/UX 深度优化 | 待打 |
