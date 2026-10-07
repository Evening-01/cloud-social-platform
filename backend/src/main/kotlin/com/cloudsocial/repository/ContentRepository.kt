package com.cloudsocial.repository

import com.cloudsocial.domain.Content
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ContentRepository : JpaRepository<Content, Long> {
    fun findByDeletedFalse(pageable: Pageable): Page<Content>
    fun findByUserIdAndDeletedFalse(userId: Long, pageable: Pageable): Page<Content>
    fun findByIdAndDeletedFalse(id: Long): Content?
    fun findByDeletedFalseAndTitleContaining(keyword: String, pageable: Pageable): Page<Content>
}
