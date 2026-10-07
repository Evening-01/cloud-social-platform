package com.cloudsocial.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

/**
 * JWT 工具类
 */
@Component
class JwtUtil(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.expiration-ms}") private val expirationMs: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    /** 生成 Token */
    fun generateToken(userId: Long, username: String): String {
        val now = Date()
        return Jwts.builder()
            .subject(username)
            .claim("userId", userId)
            .issuedAt(now)
            .expiration(Date(now.time + expirationMs))
            .signWith(key)
            .compact()
    }

    /** 解析 Token */
    private fun parseToken(token: String): Claims? = try {
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    } catch (e: Exception) {
        null
    }

    /** 校验 Token 是否有效 */
    fun validate(token: String): Boolean = parseToken(token) != null

    /** 获取用户名 */
    fun getUsername(token: String): String? = parseToken(token)?.subject

    /** 获取用户 ID */
    fun getUserId(token: String): Long? = parseToken(token)?.get("userId", Integer::class.java)?.toLong()
}
