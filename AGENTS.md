# AGENTS.md — Cloud Social Platform

> 本文件是给 AI 编程助手（Pi / Claude Code 等）与协作者的**项目总纲**。
> AI 在每次任务开始前**必须阅读本文件**，遵守其中全部约定。

## 1. 项目概述

**Cloud Social Platform（云上社交平台）** 是一个图文视频分享社交平台，支持：

- 用户注册/登录（JWT 认证）
- 图文 / 视频内容上传（分片上传、FFmpeg 转码、缩略图）
- 内容 Feed 流展示（瀑布流、懒加载、无限滚动）
- 点赞系统（Redis 实时计数 + MySQL 持久化 + 防刷限流）
- 分享链接生成（Base62 短链 + 访问追踪）
- 点赞数统计面板（个人获赞排行、趋势图表）

**目标**：交付一个部署在阿里云、可公开访问、UI/UX 体验优秀的完整应用，点赞数作为课程成绩依据。

## 2. 技术栈（强制）

| 层 | 技术 | 版本要求 |
|---|---|---|
| 后端语言 | Kotlin | 1.9+ |
| 后端框架 | Spring Boot | 3.x |
| ORM | Spring Data JPA (Hibernate) | — |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 6.0+（服务器已装） |
| 构建工具 | Gradle (Wrapper) | 8.x |
| JDK | OpenJDK | 17（本地为 21，向下兼容 target 17） |
| 前端构建 | Vite | 5.x |
| 前端语言 | TypeScript | 5.x |
| 前端框架 | React | 18.x |
| UI 组件库 | **shadcn/ui** | 最新（见 `skills/ui-ux-shadcn/SKILL.md`） |
| 样式 | Tailwind CSS | 3.x |
| 视频处理 | FFmpeg | 4.4+（服务器已装） |

## 3. 项目结构

```
cloud-social-platform/
├── AGENTS.md              # 本文件：项目总纲
├── WORKFLOW.md            # 开发流程与验收标准
├── README.md              # 项目说明
├── docs/                  # 架构设计、API 文档、部署文档
├── backend/               # Kotlin + Spring Boot 后端
│   ├── src/main/kotlin/...
│   └── src/test/kotlin/...
├── frontend/              # React + Vite + shadcn/ui 前端
├── skills/
│   └── ui-ux-shadcn/      # UI/UX 专属 skill（shadcn/ui 深度定制规范）
└── scripts/               # 部署、构建辅助脚本
```

## 4. 后端架构规范（分层 + DDD）

```
backend/src/main/kotlin/com/cloudsocial/
├── config/          # 配置类（Security、Redis、CORS、Swagger）
├── controller/      # REST 控制器（只做参数校验与转发）
├── service/         # 业务逻辑层（接口 + 实现）
├── repository/      # Spring Data JPA 仓库
├── domain/          # 领域实体（Entity）
├── dto/             # 请求/响应 DTO
├── exception/       # 全局异常处理 + 错误码
├── security/        # JWT 认证与授权
└── common/          # 统一响应、工具类
```

**数据库表（ER）**：`user`、`content`、`media_file`、`like_record`、`share_link`

**统一响应格式**：`{ code, message, data, timestamp }`，错误码 5 位数字（20000 成功 / 40001 参数错误 / 50001 服务器异常）。

## 5. 前端规范

- 组件库：**shadcn/ui**（Radix + Tailwind），严禁裸 Bootstrap 或未改默认主题
- UI 风格对标：小红书 / Instagram（瀑布流 Feed、点赞动效、沉浸式详情页）
- 必须实现：深色模式、骨架屏、图片预览器、乐观 UI（点赞即时反馈）、无障碍（WCAG 2.1 AA）

## 6. Git 规范（Git Flow + Conventional Commits）

- 分支：`main`（稳定）/ `develop`（开发）/ `feature/*` / `hotfix/*`
- 提交格式：`<type>(<scope>): <description>`，type ∈ feat/fix/refactor/docs/style/test/chore
- 每个版本完成后打 Tag：`v1.0.0`、`v1.1.0`...
- **铁律：Chrome MCP 验证通过前，严禁合并到 develop/main**

## 7. 部署信息（阿里云）

| 项 | 值 |
|---|---|
| 公网 IP | 121.40.108.157 |
| 系统 | Ubuntu 22.04.5 LTS |
| 规格 | 2 核 / 2G（已加 2G swap） |
| SSH | root，密码见主人（**禁止写入仓库**） |
| 已装 | JDK 17、MySQL 8.0、Redis 6.0、Nginx 1.18、FFmpeg 4.4 |
| 端口 | 80(Nginx) / 8080(图书项目占用) / 3306(MySQL) / 6379(Redis) |

**部署架构**：Nginx(80, 反代+静态) → Spring Boot(新项目用 **9090** 端口，避开图书项目的 8080) + MySQL + Redis + OSS。

**重要约定**：
- 图书项目 `library-server.jar`（8080）**正在运行，绝不能停止/删除**
- 新项目数据库用独立库 `social_platform`，不动 `library` 库
- 新项目后端端口用 **9090**，避免与图书项目 8080 冲突
- 内存受限（2G），新项目 JVM 参数 `-Xms256m -Xmx512m`

## 8. 禁止事项（红线）

- ❌ 禁止 MVP 简化：Base64 存图、轮询冒充实时、内存 Map 冒充缓存、假数据填充
- ❌ 禁止跳过 Chrome MCP 验证直接提交
- ❌ 禁止停掉/删除图书项目（library-server）
- ❌ 禁止把 SSH 密码、云 AK/SK 提交到 Git
- ❌ 上传文件大小必须校验：图片 ≤10MB、视频 ≤500MB
