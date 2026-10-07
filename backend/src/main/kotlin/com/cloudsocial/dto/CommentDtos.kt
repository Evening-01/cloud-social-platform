package com.cloudsocial.dto

import java.time.LocalDateTime

/**
 * 评论相关 DTO
 */
data class CommentResponse(
    val id: Long,
    val contentId: Long,
    val userId: Long,
    val author: UserResponse?,
    val content: String,
    val createdAt: LocalDateTime
)

data class CommentRequest(
    @field:jakarta.validation.constraints.NotBlank(message = "评论内容不能为空")
    @field:jakarta.validation.constraints.Size(max = 500, message = "评论最长 500 字")
    val content: String
)
