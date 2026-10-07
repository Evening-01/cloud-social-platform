# 部署文档 — Cloud Social Platform

> 本文记录从零到上线阿里云 ECS 的完整部署过程。

## 1. 服务器信息

| 项 | 值 |
|---|---|
| 公网 IP | 121.40.108.157 |
| 系统 | Ubuntu 22.04.5 LTS |
| 规格 | 2 核 / 2G（+2G swap） |
| 区域 | 华东1（杭州） |
| 访问 | http://121.40.108.157:8081 |

## 2. 环境准备（已配置完成）

| 软件 | 版本 | 说明 |
|---|---|---|
| JDK | 17.0.20 | 预装 |
| MySQL | 8.0.46 | 预装，已调优（156MB） |
| Redis | 6.0.16 | 限 128M |
| Nginx | 1.18 | 80（图书）+ 8081（社交） |
| FFmpeg | 4.4.2 | 视频转码 |

内存优化：MySQL 关闭 performance_schema（省 216MB）、2G swap 文件。

## 3. 目录结构（服务器）

```
/opt/cloud-social/
├── cloud-social-backend.jar   # 后端（74MB）
├── dist/                      # 前端构建产物
└── uploads/                   # 上传的图片/视频
```

## 4. 后端部署（systemd）

服务文件：`/etc/systemd/system/cloud-social.service`

```ini
[Service]
WorkingDirectory=/opt/cloud-social
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /opt/cloud-social/cloud-social-backend.jar
Environment=DB_HOST=127.0.0.1
Environment=DB_USER=social
Environment=DB_PASSWORD=Social@2026
Environment=DB_NAME=social_platform
Environment=REDIS_HOST=127.0.0.1
Environment=UPLOAD_DIR=/opt/cloud-social/uploads
```

管理命令：

```bash
systemctl start cloud-social     # 启动
systemctl stop cloud-social      # 停止
systemctl restart cloud-social   # 重启
journalctl -u cloud-social -f    # 查看日志
```

## 5. 前端部署（Nginx）

配置：`/etc/nginx/sites-available/social.conf`（监听 8081）

```nginx
server {
    listen 8081;
    root /opt/cloud-social/dist;
    location / { try_files $uri $uri/ /index.html; }
    location /api/ { proxy_pass http://127.0.0.1:9090; client_max_body_size 520m; }
    location /uploads/ { proxy_pass http://127.0.0.1:9090; }
}
```

## 6. 数据库

- 库：`social_platform`（utf8mb4）
- 账号：`social`（仅授权 social_platform 库）
- 表：`user`、`content`、`media_file`、`like_record`、`share_link`
- 建表 SQL：`backend/src/main/resources/schema.sql`

## 7. 安全组

入方向规则（8081 已开放）：

| 端口 | 用途 |
|---|---|
| 22 | SSH |
| 80 | 图书项目 |
| 443 | HTTPS |
| 3306 | MySQL |
| 8081 | 社交平台 |

## 8. 重新部署流程（代码更新后）

```bash
# 本地打包
cd backend && gradlew bootJar
cd frontend && npm run build

# 上传
pscp cloud-social-backend.jar root@121.40.108.157:/opt/cloud-social/
pscp -r dist/* root@121.40.108.157:/opt/cloud-social/dist/

# 重启
systemctl restart cloud-social
```

## 9. 端口约定

| 端口 | 服务 |
|---|---|
| 80 | 图书项目（Nginx） |
| 8080 | 图书项目后端 |
| 8081 | 社交平台（Nginx） |
| 9090 | 社交平台后端 |
| 3306 | MySQL |
| 6379 | Redis |
