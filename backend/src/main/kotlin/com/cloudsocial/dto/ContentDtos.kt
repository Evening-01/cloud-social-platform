package com.cloudsocial.dto

import java.time.LocalDateTime

/**
 * 内容相关 DTO
 */
data class ContentResponse(
    val id: Long,
    val userId: Long,
    val author: UserResponse? = null,
    val title: String,
    val description: String?,
    val contentType: Int,
    val likeCount: Int,
    val liked: Boolean = false,
    val mediaFiles: List<MediaFileResponse>,
    val createdAt: LocalDateTime
)

data class MediaFileResponse(
    val id: Long,
    val mediaType: Int,
    val url: String,
    val coverUrl: String?,
    val width: Int?,
    val height: Int?,
    val durationSec: Int?,
    val sortOrder: Int
)

data class PageResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
    val hasMore: Boolean
)
