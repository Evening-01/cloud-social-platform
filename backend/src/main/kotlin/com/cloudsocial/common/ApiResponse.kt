package com.cloudsocial.common

/**
 * 统一响应格式：{ code, message, data, timestamp }
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> success(data: T? = null, message: String = "成功"): ApiResponse<T> =
            ApiResponse(ErrorCode.SUCCESS.code, message, data)

        fun <T> success(data: T? = null): ApiResponse<T> =
            ApiResponse(ErrorCode.SUCCESS.code, ErrorCode.SUCCESS.message, data)

        fun <T> error(code: Int, message: String): ApiResponse<T> =
            ApiResponse(code, message, null)

        fun <T> error(errorCode: ErrorCode): ApiResponse<T> =
            ApiResponse(errorCode.code, errorCode.message, null)
    }
}

/**
 * 错误码体系（5 位数字）
 * 2xxxx 成功 / 4xxxx 客户端错误 / 5xxxx 服务端错误
 */
enum class ErrorCode(val code: Int, val message: String) {
    SUCCESS(20000, "成功"),

    // 参数错误 40xxx
    PARAM_INVALID(40001, "参数错误"),
    UNAUTHORIZED(40100, "未认证或登录已过期"),
    FORBIDDEN(40300, "无权限访问"),
    NOT_FOUND(40400, "资源不存在"),
    METHOD_NOT_ALLOWED(40500, "请求方法不支持"),
    RATE_LIMITED(42900, "操作过于频繁，请稍后再试"),

    // 业务错误 40xxx
    USERNAME_EXISTS(40010, "用户名已存在"),
    USER_NOT_FOUND(40011, "用户不存在"),
    PASSWORD_ERROR(40012, "用户名或密码错误"),
    FILE_TYPE_INVALID(40020, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(40021, "文件大小超出限制"),
    CONTENT_NOT_FOUND(40022, "内容不存在"),
    ALREADY_LIKED(40030, "请勿重复点赞"),
    NOT_LIKED(40031, "尚未点赞"),
    SHARE_EXPIRED(40040, "分享链接已过期"),
    SHARE_INVALID(40041, "分享链接无效"),

    // 服务端错误 50xxx
    SERVER_ERROR(50001, "服务器内部异常"),
    DB_ERROR(50002, "数据库异常"),
    FILE_UPLOAD_FAILED(50010, "文件上传失败"),
    VIDEO_PROCESS_FAILED(50011, "视频处理失败"),
    CACHE_ERROR(50012, "缓存服务异常");
}
