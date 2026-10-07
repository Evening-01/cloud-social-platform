package com.cloudsocial.exception

import com.cloudsocial.common.ApiResponse
import com.cloudsocial.common.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.NoHandlerFoundException

/**
 * 全局异常处理器
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /** 业务异常 */
    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.error(e.errorCode.code, e.message))

    /** 参数校验异常 */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val msg = e.bindingResult.allErrors
            .joinToString("; ") { err ->
                val field = (err as? FieldError)?.field ?: ""
                "$field: ${err.defaultMessage}"
            }
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.PARAM_INVALID.code, msg))
    }

    /** 缺少请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(e: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.ok(ApiResponse.error(ErrorCode.PARAM_INVALID.code, "缺少参数: ${e.parameterName}"))

    /** 请求体解析失败 */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.ok(ApiResponse.error(ErrorCode.PARAM_INVALID.code, "请求体格式错误"))

    /** 上传文件超限 */
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUpload(e: MaxUploadSizeExceededException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.ok(ApiResponse.error(ErrorCode.FILE_SIZE_EXCEEDED))

    /** 无权限 */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ErrorCode.FORBIDDEN))

    /** 404 */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFound(e: NoHandlerFoundException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ErrorCode.NOT_FOUND))

    /** 兜底异常 */
    @ExceptionHandler(Exception::class)
    fun handleOther(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("未处理异常", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ErrorCode.SERVER_ERROR))
    }
}
