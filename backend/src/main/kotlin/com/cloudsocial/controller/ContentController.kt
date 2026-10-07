package com.cloudsocial.controller

import com.cloudsocial.common.ApiResponse
import com.cloudsocial.dto.ContentResponse
import com.cloudsocial.dto.PageResponse
import com.cloudsocial.security.UserPrincipal
import com.cloudsocial.service.ContentService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * 内容接口
 */
@RestController
@RequestMapping("/api/content")
class ContentController(
    private val contentService: ContentService
) {

    /** 发布内容（图文/视频） */
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam("title") title: String,
        @RequestParam("description", required = false) description: String?,
        @RequestParam("files") files: List<MultipartFile>
    ): ApiResponse<ContentResponse> =
        ApiResponse.success(
            contentService.createContent(principal.userId, title, description, files),
            "发布成功"
        )

    /** Feed 流（分页） */
    @GetMapping
    fun feed(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?
    ): ApiResponse<PageResponse<ContentResponse>> =
        ApiResponse.success(contentService.getFeed(page, size, keyword))

    /** 内容详情 */
    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): ApiResponse<ContentResponse> =
        ApiResponse.success(contentService.getContent(id))
}
