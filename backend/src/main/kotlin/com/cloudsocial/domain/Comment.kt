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
 * 评论实体
 */
@Entity
@Table(
    name = "comment",
    indexes = [Index(name = "idx_content", columnList = "content_id")]
)
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "content_id", nullable = false)
    var contentId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(nullable = false, length = 500)
    var content: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
