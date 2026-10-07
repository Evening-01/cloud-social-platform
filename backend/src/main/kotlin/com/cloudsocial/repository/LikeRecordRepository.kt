package com.cloudsocial.repository

import com.cloudsocial.domain.LikeRecord
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRecordRepository : JpaRepository<LikeRecord, Long> {
    fun existsByUserIdAndContentId(userId: Long, contentId: Long): Boolean
    fun findByUserIdAndContentId(userId: Long, contentId: Long): LikeRecord?
    fun deleteByUserIdAndContentId(userId: Long, contentId: Long)
    fun countByContentId(contentId: Long): Long
    fun countByUserId(userId: Long): Long
}
