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
 * 分享链接实体
 */
@Entity
@Table(
    name = "share_link",
    indexes = [Index(name = "idx_content", columnList = "content_id")]
)
class ShareLink(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "content_id", nullable = false)
    var contentId: Long,

    @Column(name = "creator_id", nullable = false)
    var creatorId: Long,

    @Column(name = "short_code", nullable = false, unique = true, length = 8)
    var shortCode: String,

    @Column(name = "expire_days", nullable = false)
    var expireDays: Int = 0,

    @Column(name = "visit_count", nullable = false)
    var visitCount: Int = 0,

    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
