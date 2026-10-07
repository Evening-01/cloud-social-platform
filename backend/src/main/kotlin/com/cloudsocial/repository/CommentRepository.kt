package com.cloudsocial.repository

import com.cloudsocial.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByContentIdOrderByCreatedAtAsc(contentId: Long): List<Comment>
    fun countByContentId(contentId: Long): Long
}
