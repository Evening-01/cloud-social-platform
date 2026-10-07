# 运维手册 — Cloud Social Platform

## 1. 日常巡检

```bash
# 服务状态
systemctl status cloud-social nginx mysql redis-server

# 内存/磁盘
free -h && df -h /

# 端口监听
ss -tlnp | grep -E ':80|:8080|:8081|:9090|:3306|:6379'

# 后端健康检查
curl http://127.0.0.1:9090/actuator/health
```

## 2. 日志查看

```bash
# 后端日志
journalctl -u cloud-social -f --no-pager -n 100

# Nginx 日志
tail -f /var/log/nginx/access.log /var/log/nginx/error.log
```

## 3. 数据库备份

```bash
# 手动全量备份
mysqldump -usocial -p'Social@2026' social_platform > /root/backup/social_$(date +%F).sql

# 恢复
mysql -usocial -p'Social@2026' social_platform < backup.sql
```

建议配置 crontab 每日备份：

```bash
# 每天凌晨 3 点备份
0 3 * * * mysqldump -usocial -p'Social@2026' social_platform > /root/backup/social_$(date +\%F).sql
```

## 4. 常见问题排查

| 现象 | 排查 |
|---|---|
| 8081 打不开 | `systemctl status nginx` + 检查安全组 |
| 接口 500 | `journalctl -u cloud-social -n 50` 看异常 |
| 点赞不更新 | `redis-cli ping` + `redis-cli get like_count:{id}` |
| 图片 404 | 检查 `/opt/cloud-social/uploads` 目录 |
| 内存爆 | `free -h`，必要时重启后端 |

## 5. 紧急操作

```bash
# 重启后端（发布/故障恢复）
systemctl restart cloud-social

# 重启全部依赖
systemctl restart nginx mysql redis-server

# 扩容 swap（当前 2G）
fallocate -l 4G /swapfile && mkswap /swapfile && swapon /swapfile
```

## 6. 监控指标

- 后端内存：JVM 上限 512M（`-Xmx512m`）
- MySQL：已调优至 ~156MB
- Redis：maxmemory 128M
- 磁盘：40G（上传文件 + 系统）
