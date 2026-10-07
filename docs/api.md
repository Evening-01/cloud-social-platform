# API 文档 — Cloud Social Platform

> 基础地址：`http://121.40.108.157:8081` ｜ 统一响应格式：`{code, message, data, timestamp}`

## 统一响应格式

```json
{
  "code": 20000,
  "message": "成功",
  "data": { },
  "timestamp": 1791361449592
}
```

**错误码体系**（5 位数字）：

| 码 | 含义 |
|---|---|
| 20000 | 成功 |
| 40001 | 参数错误 |
| 40010 | 用户名已存在 |
| 40012 | 用户名或密码错误 |
| 40020 | 不支持的文件类型 |
| 40021 | 文件大小超出限制 |
| 40022 | 内容不存在 |
| 40040/40041 | 分享链接过期/无效 |
| 40100 | 未认证或登录过期 |
| 42900 | 操作过于频繁 |
| 50001 | 服务器内部异常 |

## 认证说明

除标注「公开」的接口外，均需在请求头携带：

```
Authorization: Bearer <token>
```

## 1. 用户认证

### 注册（公开）
`POST /api/auth/register`

```json
{ "username": "testuser", "password": "123456", "nickname": "测试用户" }
```

### 登录（公开）
`POST /api/auth/login`

```json
{ "username": "testuser", "password": "123456" }
```

响应 `data`：`{ token, user: { id, username, nickname, avatarUrl, email } }`

### 当前用户
`GET /api/auth/me`

## 2. 内容

### 发布内容（需登录）
`POST /api/content`（multipart/form-data）

| 字段 | 类型 | 说明 |
|---|---|---|
| title | string | 标题 |
| description | string? | 描述 |
| files | file[] | 图片/视频，最多 9 个 |

限制：图片 ≤10MB（jpg/png/webp/gif）、视频 ≤500MB（mp4/webm/mov）

### Feed 流（公开）
`GET /api/content?page=0&size=10&keyword=`

### 内容详情（公开）
`GET /api/content/{id}`

## 3. 点赞

| 接口 | 方法 | 说明 |
|---|---|---|
| `/api/like/{contentId}` | POST | 点赞（幂等，需登录） |
| `/api/like/{contentId}` | DELETE | 取消点赞（需登录） |
| `/api/like/{contentId}/count` | GET | 点赞数（公开） |
| `/api/like/{contentId}/users` | GET | 点赞用户列表 |

限流：同一用户对同一内容 10 次/分钟。

## 4. 分享

| 接口 | 方法 | 说明 |
|---|---|---|
| `/api/share/{contentId}?expireDays=0` | POST | 生成短链（需登录） |
| `/api/share/{code}` | GET | 访问短链落地页数据（公开） |
| `/api/share/mine` | GET | 我的分享列表（需登录） |

短码：8 位 Base62，如 `EsGqHrmC`。
