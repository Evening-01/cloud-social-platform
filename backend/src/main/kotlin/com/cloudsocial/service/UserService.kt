package com.cloudsocial.service

import com.cloudsocial.common.ErrorCode
import com.cloudsocial.domain.User
import com.cloudsocial.dto.LoginRequest
import com.cloudsocial.dto.LoginResponse
import com.cloudsocial.dto.RegisterRequest
import com.cloudsocial.dto.UserResponse
import com.cloudsocial.exception.BusinessException
import com.cloudsocial.repository.UserRepository
import com.cloudsocial.security.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 用户服务
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    /** 注册 */
    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw BusinessException(ErrorCode.USERNAME_EXISTS)
        }
        val user = User(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password),
            nickname = request.nickname
        )
        return userRepository.save(user).toResponse()
    }

    /** 登录 */
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { BusinessException(ErrorCode.PASSWORD_ERROR) }
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw BusinessException(ErrorCode.PASSWORD_ERROR)
        }
        val token = jwtUtil.generateToken(user.id, user.username)
        return LoginResponse(token = token, user = user.toResponse())
    }

    /** 获取用户信息 */
    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { BusinessException(ErrorCode.USER_NOT_FOUND) }
        return user.toResponse()
    }

    private fun User.toResponse() = UserResponse(
        id = id,
        username = username,
        nickname = nickname,
        avatarUrl = avatarUrl,
        email = email
    )
}
