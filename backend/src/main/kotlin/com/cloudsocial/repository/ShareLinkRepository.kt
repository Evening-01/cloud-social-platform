package com.cloudsocial.repository

import com.cloudsocial.domain.ShareLink
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ShareLinkRepository : JpaRepository<ShareLink, Long> {
    fun findByShortCode(shortCode: String): Optional<ShareLink>
    fun findByCreatorId(creatorId: Long): List<ShareLink>
}
