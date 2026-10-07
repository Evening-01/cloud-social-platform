package com.cloudsocial.repository

import com.cloudsocial.domain.MediaFile
import org.springframework.data.jpa.repository.JpaRepository

interface MediaFileRepository : JpaRepository<MediaFile, Long> {
    fun findByContentIdOrderBySortOrderAsc(contentId: Long): List<MediaFile>
}
