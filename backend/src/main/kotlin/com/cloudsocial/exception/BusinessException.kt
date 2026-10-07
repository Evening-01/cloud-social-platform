package com.cloudsocial.exception

import com.cloudsocial.common.ErrorCode

/**
 * 业务异常
 */
open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)

/**
 * 未认证异常
 */
class UnauthorizedException(message: String = ErrorCode.UNAUTHORIZED.message) :
    BusinessException(ErrorCode.UNAUTHORIZED, message)

/**
 * 资源不存在异常
 */
class NotFoundException(message: String = ErrorCode.NOT_FOUND.message) :
    BusinessException(ErrorCode.NOT_FOUND, message)
