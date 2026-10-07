package com.cloudsocial.controller

import com.cloudsocial.common.ApiResponse
import com.cloudsocial.dto.CommentRequest
import com.cloudsocial.dto.CommentResponse
import com.cloudsocial.security.UserPrincipal
import com.cloudsocial.service.CommentService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 评论接口
 */
@RestController
@RequestMapping("/api/comment")
class CommentController(
    private val commentService: CommentService
) {

    /** 评论列表（公开） */
    @GetMapping("/{contentId}")
    fun list(@PathVariable contentId: Long): ApiResponse<List<CommentResponse>> =
        ApiResponse.success(commentService.list(contentId))

    /** 评论数（公开） */
    @GetMapping("/{contentId}/count")
    fun count(@PathVariable contentId: Long): ApiResponse<Long> =
        ApiResponse.success(commentService.count(contentId))

    /** 发表评论（需登录） */
    @PostMapping("/{contentId}")
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable contentId: Long,
        @Valid @RequestBody request: CommentRequest
    ): ApiResponse<CommentResponse> =
        ApiResponse.success(commentService.create(principal.userId, contentId, request.content), "评论成功")

    /** 删除评论（需登录，只能删自己的） */
    @DeleteMapping("/{commentId}")
    fun delete(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable commentId: Long
    ): ApiResponse<Nothing> {
        commentService.delete(principal.userId, commentId)
        return ApiResponse.success(null, "已删除")
    }
}
