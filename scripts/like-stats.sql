-- 点赞数统计脚本（成绩依据导出）
-- 用法: mysql -usocial -p'Social@2026' social_platform < like-stats.sql

-- 1. 个人获赞总数排行榜（成绩核心依据）
SELECT
  u.id AS 用户ID,
  u.username AS 用户名,
  u.nickname AS 昵称,
  COUNT(DISTINCT c.id) AS 发布内容数,
  COUNT(l.id) AS 获赞总数
FROM user u
LEFT JOIN content c ON c.user_id = u.id AND c.deleted = 0
LEFT JOIN like_record l ON l.content_id = c.id
GROUP BY u.id, u.username, u.nickname
ORDER BY 获赞总数 DESC;

-- 2. 内容获赞排行榜
SELECT
  c.id AS 内容ID,
  c.title AS 标题,
  u.nickname AS 作者,
  COUNT(l.id) AS 获赞数
FROM content c
JOIN user u ON u.id = c.user_id
LEFT JOIN like_record l ON l.content_id = c.id
WHERE c.deleted = 0
GROUP BY c.id, c.title, u.nickname
ORDER BY 获赞数 DESC;

-- 3. 分享链接访问统计
SELECT
  s.short_code AS 短码,
  c.title AS 内容,
  s.visit_count AS 访问次数,
  s.created_at AS 创建时间
FROM share_link s
JOIN content c ON c.id = s.content_id
ORDER BY s.visit_count DESC;

-- 导出 CSV 示例:
-- mysql -usocial -p'Social@2026' social_platform -e "
--   SELECT u.username, u.nickname, COUNT(l.id) AS likes
--   FROM user u LEFT JOIN content c ON c.user_id=u.id
--   LEFT JOIN like_record l ON l.content_id=c.id
--   GROUP BY u.id ORDER BY likes DESC;" > 点赞统计.csv
