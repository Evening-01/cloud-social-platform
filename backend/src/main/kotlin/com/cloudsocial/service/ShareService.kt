package com.cloudsocial.service

import com.cloudsocial.common.ErrorCode
import com.cloudsocial.domain.ShareLink
import com.cloudsocial.dto.ContentResponse
import com.cloudsocial.exception.BusinessException
import com.cloudsocial.repository.ContentRepository
import com.cloudsocial.repository.MediaFileRepository
import com.cloudsocial.repository.ShareLinkRepository
import com.cloudsocial.repository.UserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.LocalDateTime

/**
 * 分享链接服务（Base62 短链 + 访问追踪）
 */
@Service
class ShareService(
    private val shareLinkRepository: ShareLinkRepository,
    private val contentRepository: ContentRepository,
    private val mediaFileRepository: MediaFileRepository,
    private val userRepository: UserRepository,
    private val redis: StringRedisTemplate
) {

    companion object {
        private const val BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val CODE_LENGTH = 8
        private val random = SecureRandom()
    }

    /** 创建分享链接 */
    @Transactional
    fun createShareLink(userId: Long, contentId: Long, expireDays: Int): ShareLink {
        if (!contentRepository.existsById(contentId)) {
            throw BusinessException(ErrorCode.CONTENT_NOT_FOUND)
        }
        val code = generateUniqueCode()
        val expiresAt = if (expireDays > 0) LocalDateTime.now().plusDays(expireDays.toLong()) else null
        val link = ShareLink(
            contentId = contentId,
            creatorId = userId,
            shortCode = code,
            expireDays = expireDays,
            expiresAt = expiresAt
        )
        shareLinkRepository.save(link)
        // 缓存映射
        redis.opsForValue().set("share:$code", contentId.toString())
        return link
    }

    /** 通过短码访问（含追踪） */
    fun resolveShareLink(code: String): ContentResponse {
        val link = shareLinkRepository.findByShortCode(code)
            .orElseThrow { BusinessException(ErrorCode.SHARE_INVALID) }

        // 过期校验
        val expiresAt = link.expiresAt
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw BusinessException(ErrorCode.SHARE_EXPIRED)
        }

        // 访问计数
        link.visitCount++
        shareLinkRepository.save(link)

        val content = contentRepository.findByIdAndDeletedFalse(link.contentId)
            ?: throw BusinessException(ErrorCode.CONTENT_NOT_FOUND)
        val mediaFiles = mediaFileRepository.findByContentIdOrderBySortOrderAsc(content.id)
        val author = userRepository.findById(content.userId).orElse(null)

        return ContentResponse(
            id = content.id,
            userId = content.userId,
            author = author?.let {
                com.cloudsocial.dto.UserResponse(it.id, it.username, it.nickname, it.avatarUrl, it.email)
            },
            title = content.title,
            description = content.description,
            contentType = content.contentType,
            likeCount = content.likeCount,
            mediaFiles = mediaFiles.map {
                com.cloudsocial.dto.MediaFileResponse(
                    it.id, it.mediaType, it.ossUrl, it.coverUrl,
                    it.width, it.height, it.durationSec, it.sortOrder
                )
            },
            createdAt = content.createdAt
        )
    }

    /** 我的分享列表 */
    fun myShares(userId: Long): List<ShareLink> = shareLinkRepository.findByCreatorId(userId)

    /** 生成唯一短码 */
    private fun generateUniqueCode(): String {
        repeat(10) {
            val sb = StringBuilder(CODE_LENGTH)
            repeat(CODE_LENGTH) { sb.append(BASE62[random.nextInt(BASE62.length)]) }
            val code = sb.toString()
            if (!shareLinkRepository.findByShortCode(code).isPresent) return code
        }
        throw BusinessException(ErrorCode.SERVER_ERROR, "短码生成失败，请重试")
    }
}
