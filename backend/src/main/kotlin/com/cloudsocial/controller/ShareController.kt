package com.cloudsocial.controller

import com.cloudsocial.common.ApiResponse
import com.cloudsocial.domain.ShareLink
import com.cloudsocial.dto.ContentResponse
import com.cloudsocial.security.UserPrincipal
import com.cloudsocial.service.ShareService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 分享链接接口
 */
@RestController
@RequestMapping("/api/share")
class ShareController(
    private val shareService: ShareService
) {

    /** 创建分享链接（需认证） */
    @PostMapping("/{contentId}")
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable contentId: Long,
        @RequestParam(defaultValue = "0") expireDays: Int
    ): ApiResponse<ShareLink> =
        ApiResponse.success(shareService.createShareLink(principal.userId, contentId, expireDays), "分享链接已生成")

    /** 访问短链（公开，落地页数据） */
    @GetMapping("/{code}")
    fun resolve(@PathVariable code: String): ApiResponse<ContentResponse> =
        ApiResponse.success(shareService.resolveShareLink(code))

    /** 我的分享列表（需认证） */
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal): ApiResponse<List<ShareLink>> =
        ApiResponse.success(shareService.myShares(principal.userId))
}
