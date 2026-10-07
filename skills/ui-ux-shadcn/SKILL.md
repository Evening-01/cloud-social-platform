---
name: ui-ux-shadcn
description: 基于 shadcn/ui（GitHub 125k★ 最高星 UI 组件库）的社交平台 UI/UX 深度定制规范。当需要设计、开发或打磨图文视频 Feed 流的界面、交互、动效、响应式与无障碍时使用本 skill。
---

# UI/UX Skill — shadcn/ui 深度定制

## 1. 为什么是 shadcn/ui

- 项目源：`shadcn-ui/ui`（GitHub **125,218★**，本项目筛选出的 star 最高 UI 方案）
- 官网：https://ui.shadcn.com
- 本质：**可复制、可定制的组件源码**（不是黑盒 npm 依赖），基于 Radix UI + Tailwind CSS
- 优势：组件代码直接落进项目 `components/ui/`，可随意改，主题用 CSS 变量控制

## 2. 安装与初始化

```bash
cd frontend
# 1. 初始化（选择 TypeScript + Tailwind CSS）
npx shadcn@latest init

# 2. 按需添加组件（本项目核心清单）
npx shadcn@latest add button card input textarea avatar badge
npx shadcn@latest add dialog dropdown-menu tabs skeleton separator
npx shadcn@latest add toast tooltip switch sheet carousel
npx shadcn@latest add form label select sonner
```

## 3. 社交 Feed 流核心组件映射

| 功能 | shadcn/ui 组件 | 定制要点 |
|---|---|---|
| Feed 瀑布流 | `Masonry`（自定义 + CSS columns） | 图文视频混排，`break-inside-avoid` |
| 内容卡片 | `Card` | 圆角 16px、hover 阴影、作者头像 |
| 发布弹窗 | `Dialog` + `Form` | 分片上传进度条 |
| 点赞按钮 | `Button`（variant=ghost）+ 自定义粒子 | 乐观 UI，即时反馈 |
| 图片预览器 | `Sheet` / 自定义 lightbox | 缩放、滑动切换、保存 |
| 分享弹窗 | `Dialog` + QRCode + 复制 | Toast 提示复制成功 |
| 统计面板 | `Tabs` + `Card` + ECharts | 日/周/总榜切换 |
| 骨架屏 | `Skeleton` | Feed 加载占位 |
| 深色模式 | ThemeProvider + `next-themes` | 持久化用户偏好 |

## 4. 设计 Token（对标小红书/Instagram）

```css
:root {
  /* 品牌色：小红书红 → 调整为更现代的珊瑚红 */
  --primary: 255 90 95;        /* #FF5A5F */
  --primary-foreground: 255 255 255;
  /* 圆角 */
  --radius: 0.875rem;          /* 卡片 14px */
  /* 间距 */
  --feed-gap: 16px;
  /* 动效 */
  --transition-fast: 150ms;
}
```

- 字体：系统字体栈 + 中文优化（`PingFang SC`, `Microsoft YaHei`）
- 卡片：白底、圆角 14-16px、`box-shadow` 柔和
- 点赞动效：点击瞬间缩放 1.2x + 粒子飘散 + 数字滚动

## 5. 必须实现的 UI/UX 亮点（成绩加分项）

1. **瀑布流 Feed**：双列/三列自适应，图片懒加载 + 占位色
2. **点赞动效**：粒子特效 + 头像堆叠 + 飘心动画
3. **图片预览器**：双指缩放、左右滑动、长按保存
4. **视频播放**：hover 自动播放、全屏、进度条（video.js/plyr）
5. **骨架屏**：内容加载时显示灰块占位（Skeleton）
6. **乐观 UI**：点赞/评论即时反馈，失败回滚
7. **深色模式**：一键切换 + 记忆偏好
8. **空状态/错误状态**：插画 + 引导文案
9. **响应式**：移动端单列 → 桌面端三列
10. **无障碍**：ARIA 标签、键盘导航、对比度 AA

## 6. 禁则

- ❌ 禁止直接套用未改动的默认主题（必须定制品牌色与圆角）
- ❌ 禁止用 Bootstrap 默认样式或简单静态模板
- ❌ 禁止像素级粗糙的动效（所有动效需流畅 ≥60fps）

## 7. 验证标准（配合 Chrome MCP）

每次 UI 改动后，用 Chrome DevTools MCP 验证：
- Lighthouse 性能 ≥85、无障碍 ≥90、SEO ≥80
- 移动端 375px / 桌面端 1440px 响应式合格
- 点赞、上传、分享交互链路完整
