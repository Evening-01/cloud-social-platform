package com.cloudsocial.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

/**
 * 点赞记录实体（联合唯一索引防重复点赞）
 */
@Entity
@Table(
    name = "like_record",
    uniqueConstraints = [UniqueConstraint(name = "uk_user_content", columnNames = ["user_id", "content_id"])],
    indexes = [Index(name = "idx_content_created", columnList = "content_id,created_at")]
)
class LikeRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "content_id", nullable = false)
    var contentId: Long,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
