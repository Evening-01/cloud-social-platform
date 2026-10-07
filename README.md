# Cloud Social Platform 云上社交平台

图文视频分享社交平台 —— 支持内容上传、瀑布流 Feed、点赞、分享链接与点赞统计，部署于阿里云。

> 课程作业项目，UI/UX 体验优先，点赞数作为成绩依据。

## 技术栈

- **后端**：Kotlin 1.9 + Spring Boot 3.x + Spring Data JPA + MySQL 8.0 + Redis
- **前端**：React 18 + TypeScript + Vite + **shadcn/ui**（125k★）+ Tailwind CSS
- **视频处理**：FFmpeg（转码 + 缩略图）
- **部署**：阿里云 ECS（Ubuntu 22.04）+ Nginx + HTTPS

## 项目结构

```
cloud-social-platform/
├── AGENTS.md       # 项目总纲（AI 必读）
├── WORKFLOW.md     # 7 步开发流程与验收标准
├── docs/           # 架构/API/部署文档
├── backend/        # Kotlin + Spring Boot 后端
├── frontend/       # React + Vite + shadcn/ui 前端
├── skills/         # UI/UX skill（shadcn/ui 定制规范）
└── scripts/        # 部署与构建脚本
```

## 快速开始

```bash
# 后端（端口 9090）
cd backend && ./gradlew bootRun

# 前端（开发模式）
cd frontend && npm install && npm run dev
```

## 部署

- 服务器：121.40.108.157（阿里云 ECS，Ubuntu 22.04，2C2G + 2G swap）
- 架构：Nginx(80) → Spring Boot(9090) + MySQL(3306) + Redis(6379) + OSS
- 详见 `docs/` 部署文档

## 版本

| 版本 | 内容 |
|---|---|
| v1.0.0 | 上传 + 展示 |
| v1.1.0 | 点赞系统 |
| v1.2.0 | 分享链接 |
| v1.3.0 | UI/UX 深度优化 |
