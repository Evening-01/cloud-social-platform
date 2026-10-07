package com.cloudsocial.controller

import com.cloudsocial.common.ApiResponse
import com.cloudsocial.dto.UserResponse
import com.cloudsocial.security.UserPrincipal
import com.cloudsocial.service.LikeService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 点赞接口
 */
@RestController
@RequestMapping("/api/like")
class LikeController(
    private val likeService: LikeService
) {

    /** 点赞 */
    @PostMapping("/{contentId}")
    fun like(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable contentId: Long
    ): ApiResponse<Int> =
        ApiResponse.success(likeService.like(principal.userId, contentId), "点赞成功")

    /** 取消点赞 */
    @DeleteMapping("/{contentId}")
    fun unlike(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable contentId: Long
    ): ApiResponse<Int> =
        ApiResponse.success(likeService.unlike(principal.userId, contentId), "已取消点赞")

    /** 当前点赞数 */
    @GetMapping("/{contentId}/count")
    fun count(@PathVariable contentId: Long): ApiResponse<Int> =
        ApiResponse.success(likeService.currentCount(contentId))

    /** 点赞用户列表 */
    @GetMapping("/{contentId}/users")
    fun users(@PathVariable contentId: Long): ApiResponse<List<UserResponse>> =
        ApiResponse.success(likeService.getLikedUsers(contentId))
}
