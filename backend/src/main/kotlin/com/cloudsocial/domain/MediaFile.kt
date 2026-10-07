package com.cloudsocial.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

/**
 * 媒体文件实体
 * media_type: 1=图片 2=视频
 */
@Entity
@Table(
    name = "media_file",
    indexes = [Index(name = "idx_content", columnList = "content_id")]
)
class MediaFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "content_id", nullable = false)
    var contentId: Long,

    @Column(name = "media_type", nullable = false)
    var mediaType: Int,

    @Column(name = "oss_url", nullable = false, length = 512)
    var ossUrl: String,

    @Column(name = "cover_url", length = 512)
    var coverUrl: String? = null,

    var width: Int? = null,

    var height: Int? = null,

    @Column(name = "file_size")
    var fileSize: Long? = null,

    @Column(name = "duration_sec")
    var durationSec: Int? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
)
