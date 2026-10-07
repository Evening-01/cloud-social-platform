package com.cloudsocial.controller

import com.cloudsocial.common.ApiResponse
import com.cloudsocial.dto.LoginRequest
import com.cloudsocial.dto.LoginResponse
import com.cloudsocial.dto.RegisterRequest
import com.cloudsocial.dto.UserResponse
import com.cloudsocial.security.UserPrincipal
import com.cloudsocial.service.UserService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 认证与用户接口
 */
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService
) {

    /** 注册 */
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ApiResponse<UserResponse> =
        ApiResponse.success(userService.register(request), "注册成功")

    /** 登录 */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ApiResponse<LoginResponse> =
        ApiResponse.success(userService.login(request), "登录成功")

    /** 当前用户信息 */
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: UserPrincipal): ApiResponse<UserResponse> =
        ApiResponse.success(userService.getUserById(principal.userId))
}
