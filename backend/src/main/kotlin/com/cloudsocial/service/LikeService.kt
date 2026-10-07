package com.cloudsocial.service

import com.cloudsocial.common.ErrorCode
import com.cloudsocial.domain.LikeRecord
import com.cloudsocial.dto.UserResponse
import com.cloudsocial.exception.BusinessException
import com.cloudsocial.repository.ContentRepository
import com.cloudsocial.repository.LikeRecordRepository
import com.cloudsocial.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

/**
 * 点赞服务（Redis 实时计数 + MySQL 持久化 + 防刷限流）
 */
@Service
class LikeService(
    private val likeRecordRepository: LikeRecordRepository,
    private val contentRepository: ContentRepository,
    private val userRepository: UserRepository,
    private val redis: StringRedisTemplate
) {

    private val log = LoggerFactory.getLogger(LikeService::class.java)

    companion object {
        const val LIKE_COUNT_KEY = "like_count"
        const val RATE_LIMIT = 10            // 每分钟最多 10 次
        const val RATE_WINDOW_SECONDS = 60L
    }

    /** 点赞（幂等） */
    @Transactional
    fun like(userId: Long, contentId: Long): Int {
        checkContentExists(contentId)
        checkRateLimit(userId, contentId)

        // 已点赞则幂等返回
        if (likeRecordRepository.existsByUserIdAndContentId(userId, contentId)) {
            return currentCount(contentId)
        }

        // 写 MySQL（唯一索引兜底）
        try {
            likeRecordRepository.save(LikeRecord(userId = userId, contentId = contentId))
        } catch (e: Exception) {
            // 唯一索引冲突 = 已点赞，幂等处理
            return currentCount(contentId)
        }

        // Redis 计数 +1
        val newCount = redis.opsForValue().increment("$LIKE_COUNT_KEY:$contentId") ?: 1
        // 同步更新 MySQL content.like_count（保证 Feed 查询一致）
        contentRepository.findById(contentId).ifPresent { c ->
            c.likeCount = newCount.toInt()
            contentRepository.save(c)
        }
        return newCount.toInt()
    }

    /** 取消点赞（幂等） */
    @Transactional
    fun unlike(userId: Long, contentId: Long): Int {
        checkContentExists(contentId)
        checkRateLimit(userId, contentId)

        val record = likeRecordRepository.findByUserIdAndContentId(userId, contentId)
            ?: return currentCount(contentId)  // 未点赞，幂等返回

        likeRecordRepository.delete(record)

        // Redis 计数 -1（不低于 0）
        val current = currentCount(contentId)
        if (current > 0) {
            redis.opsForValue().decrement("$LIKE_COUNT_KEY:$contentId")
        }
        val newCount = (current - 1).coerceAtLeast(0)
        // 同步更新 MySQL content.like_count
        contentRepository.findById(contentId).ifPresent { c ->
            c.likeCount = newCount
            contentRepository.save(c)
        }
        return newCount
    }

    /** 当前点赞数（优先 Redis，回源 MySQL） */
    fun currentCount(contentId: Long): Int {
        redis.opsForValue().get("$LIKE_COUNT_KEY:$contentId")?.toIntOrNull()?.let { return it }
        val dbCount = likeRecordRepository.countByContentId(contentId).toInt()
        redis.opsForValue().set("$LIKE_COUNT_KEY:$contentId", dbCount.toString())
        return dbCount
    }

    /** 是否已点赞 */
    fun hasLiked(userId: Long, contentId: Long): Boolean =
        likeRecordRepository.existsByUserIdAndContentId(userId, contentId)

    /** 个人获赞总数 */
    fun userLikeTotal(userId: Long): Long = likeRecordRepository.countByUserId(userId)

    /** 点赞排行榜（某内容的点赞用户列表） */
    fun getLikedUsers(contentId: Long): List<UserResponse> =
        likeRecordRepository.findAll().asSequence()
            .filter { it.contentId == contentId }
            .mapNotNull { userRepository.findById(it.userId).orElse(null) }
            .map { UserResponse(it.id, it.username, it.nickname, it.avatarUrl, it.email) }
            .toList()

    /** 滑动窗口限流：同一用户对同一内容 10 次/分钟 */
    private fun checkRateLimit(userId: Long, contentId: Long) {
        val key = "like_rate:$userId:$contentId"
        val now = Instant.now().toEpochMilli()
        val windowStart = now - RATE_WINDOW_SECONDS * 1000
        val ops = redis.opsForZSet()
        ops.removeRangeByScore(key, 0.0, windowStart.toDouble())
        val count = ops.zCard(key) ?: 0
        if (count >= RATE_LIMIT) {
            throw BusinessException(ErrorCode.RATE_LIMITED)
        }
        ops.add(key, now.toString(), now.toDouble())
        redis.expire(key, Duration.ofSeconds(RATE_WINDOW_SECONDS))
    }

    private fun checkContentExists(contentId: Long) {
        if (!contentRepository.existsById(contentId)) {
            throw BusinessException(ErrorCode.CONTENT_NOT_FOUND)
        }
    }
}
