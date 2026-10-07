package com.cloudsocial.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 用户相关 DTO
 */
data class RegisterRequest(
    @field:NotBlank(message = "用户名不能为空")
    @field:Size(min = 3, max = 64, message = "用户名长度 3-64 位")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
    val username: String,

    @field:NotBlank(message = "密码不能为空")
    @field:Size(min = 6, max = 64, message = "密码长度 6-64 位")
    val password: String,

    @field:NotBlank(message = "昵称不能为空")
    @field:Size(max = 64, message = "昵称最长 64 位")
    val nickname: String
)

data class LoginRequest(
    @field:NotBlank(message = "用户名不能为空")
    val username: String,

    @field:NotBlank(message = "密码不能为空")
    val password: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val avatarUrl: String?,
    val email: String?
)

data class LoginResponse(
    val token: String,
    val user: UserResponse
)
