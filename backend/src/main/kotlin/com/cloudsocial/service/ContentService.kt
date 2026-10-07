package com.cloudsocial.service

import com.cloudsocial.common.ErrorCode
import com.cloudsocial.domain.Content
import com.cloudsocial.domain.MediaFile
import com.cloudsocial.dto.ContentResponse
import com.cloudsocial.dto.MediaFileResponse
import com.cloudsocial.dto.PageResponse
import com.cloudsocial.exception.BusinessException
import com.cloudsocial.repository.ContentRepository
import com.cloudsocial.repository.LikeRecordRepository
import com.cloudsocial.repository.MediaFileRepository
import com.cloudsocial.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

/**
 * 内容服务
 */
@Service
class ContentService(
    private val contentRepository: ContentRepository,
    private val mediaFileRepository: MediaFileRepository,
    private val userRepository: UserRepository,
    private val likeRecordRepository: LikeRecordRepository,
    private val fileStorageService: FileStorageService
) {

    /** 创建内容（图文/视频） */
    @Transactional
    fun createContent(
        userId: Long,
        title: String,
        description: String?,
        files: List<MultipartFile>
    ): ContentResponse {
        if (files.isEmpty()) {
            throw BusinessException(ErrorCode.PARAM_INVALID, "至少上传一个文件")
        }
        if (files.size > 9) {
            throw BusinessException(ErrorCode.PARAM_INVALID, "最多上传 9 个文件")
        }

        // 判断内容类型
        val hasVideo = files.any { isVideo(it) }
        val contentType = when {
            hasVideo && files.size > 1 -> 3  // 混合
            hasVideo -> 2                     // 视频
            else -> 1                         // 图文
        }

        // 保存内容
        val content = Content(
            userId = userId,
            title = title,
            description = description,
            contentType = contentType
        )
        contentRepository.save(content)

        // 保存媒体文件
        val mediaResponses = mutableListOf<MediaFileResponse>()
        files.forEachIndexed { index, file ->
            val stored = fileStorageService.saveMediaFile(file)
            val mediaFile = MediaFile(
                contentId = content.id,
                mediaType = stored.mediaType,
                ossUrl = "/uploads/${stored.relativePath}",
                coverUrl = stored.coverPath?.let { "/uploads/$it" },
                width = stored.width,
                height = stored.height,
                fileSize = stored.fileSize,
                durationSec = stored.durationSec,
                sortOrder = index
            )
            mediaFileRepository.save(mediaFile)
            mediaResponses.add(mediaFile.toResponse())
        }

        return content.toResponse(mediaResponses, author = userRepository.findById(userId).orElse(null), liked = false)
    }

    /** Feed 流查询（分页） */
    fun getFeed(page: Int, size: Int, keyword: String? = null, viewerId: Long? = null): PageResponse<ContentResponse> {
        val pageable = PageRequest.of(page, size.coerceIn(1, 50))
        val result = if (keyword.isNullOrBlank()) {
            contentRepository.findByDeletedFalse(pageable)
        } else {
            contentRepository.findByDeletedFalseAndTitleContaining(keyword, pageable)
        }
        val items = result.content.map { content ->
            val mediaFiles = mediaFileRepository.findByContentIdOrderBySortOrderAsc(content.id)
            val liked = viewerId?.let { likeRecordRepository.existsByUserIdAndContentId(it, content.id) } ?: false
            content.toResponse(
                mediaFiles.map { it.toResponse() },
                author = userRepository.findById(content.userId).orElse(null),
                liked = liked
            )
        }
        return PageResponse(
            items = items,
            page = page,
            size = size,
            total = result.totalElements,
            hasMore = result.hasNext()
        )
    }

    /** 内容详情 */
    fun getContent(id: Long, viewerId: Long? = null): ContentResponse {
        val content = contentRepository.findByIdAndDeletedFalse(id)
            ?: throw BusinessException(ErrorCode.CONTENT_NOT_FOUND)
        val mediaFiles = mediaFileRepository.findByContentIdOrderBySortOrderAsc(content.id)
        val liked = viewerId?.let { likeRecordRepository.existsByUserIdAndContentId(it, content.id) } ?: false
        return content.toResponse(
            mediaFiles.map { it.toResponse() },
            author = userRepository.findById(content.userId).orElse(null),
            liked = liked
        )
    }

    private fun isVideo(file: MultipartFile): Boolean {
        val ext = file.originalFilename?.substringAfterLast('.', "")?.lowercase() ?: ""
        return ext in setOf("mp4", "webm", "mov")
    }

    private fun Content.toResponse(
        mediaFiles: List<MediaFileResponse>,
        author: com.cloudsocial.domain.User?,
        liked: Boolean = false
    ): ContentResponse = ContentResponse(
        id = id,
        userId = userId,
        author = author?.let {
            com.cloudsocial.dto.UserResponse(
                id = it.id, username = it.username,
                nickname = it.nickname, avatarUrl = it.avatarUrl, email = it.email
            )
        },
        title = title,
        description = description,
        contentType = contentType,
        likeCount = likeCount,
        liked = liked,
        mediaFiles = mediaFiles,
        createdAt = createdAt
    )

    private fun MediaFile.toResponse() = MediaFileResponse(
        id = id, mediaType = mediaType, url = ossUrl, coverUrl = coverUrl,
        width = width, height = height, durationSec = durationSec, sortOrder = sortOrder
    )
}
