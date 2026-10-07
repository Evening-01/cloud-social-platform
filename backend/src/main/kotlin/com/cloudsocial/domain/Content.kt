package com.cloudsocial.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 内容（帖子）实体
 * content_type: 1=图文 2=视频 3=混合
 */
@Entity
@Table(
    name = "content",
    indexes = [
        Index(name = "idx_user_created", columnList = "user_id,created_at"),
        Index(name = "idx_created_at", columnList = "created_at")
    ]
)
class Content(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "content_type", nullable = false)
    var contentType: Int = 1,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @Column(nullable = false)
    var deleted: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
