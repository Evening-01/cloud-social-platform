package com.cloudsocial.service

import com.cloudsocial.common.ErrorCode
import com.cloudsocial.domain.Comment
import com.cloudsocial.dto.CommentResponse
import com.cloudsocial.dto.UserResponse
import com.cloudsocial.exception.BusinessException
import com.cloudsocial.repository.CommentRepository
import com.cloudsocial.repository.ContentRepository
import com.cloudsocial.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 评论服务
 */
@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val contentRepository: ContentRepository,
    private val userRepository: UserRepository
) {

    /** 评论列表 */
    fun list(contentId: Long): List<CommentResponse> =
        commentRepository.findByContentIdOrderByCreatedAtAsc(contentId)
            .map { it.toResponse() }

    /** 发表评论 */
    @Transactional
    fun create(userId: Long, contentId: Long, content: String): CommentResponse {
        if (!contentRepository.existsById(contentId)) {
            throw BusinessException(ErrorCode.CONTENT_NOT_FOUND)
        }
        val comment = Comment(contentId = contentId, userId = userId, content = content.trim())
        commentRepository.save(comment)
        return comment.toResponse()
    }

    /** 删除评论（只能删自己的） */
    @Transactional
    fun delete(userId: Long, commentId: Long) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND, "评论不存在") }
        if (comment.userId != userId) {
            throw BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的评论")
        }
        commentRepository.delete(comment)
    }

    /** 评论数 */
    fun count(contentId: Long): Long = commentRepository.countByContentId(contentId)

    private fun Comment.toResponse(): CommentResponse {
        val author = userRepository.findById(userId).orElse(null)
        return CommentResponse(
            id = id,
            contentId = contentId,
            userId = userId,
            author = author?.let {
                UserResponse(it.id, it.username, it.nickname, it.avatarUrl, it.email)
            },
            content = content,
            createdAt = createdAt
        )
    }
}
