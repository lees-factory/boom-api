package io.lees.boom.core.api.controller

import io.lees.boom.core.error.CoreErrorKind
import io.lees.boom.core.error.CoreErrorLevel
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.core.support.error.CoreApiErrorType
import io.lees.boom.core.support.error.CoreApiException
import io.lees.boom.core.support.response.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class ApiControllerAdvice {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ApiResponse<Unit> {
        // 보안상/노이즈 방지를 위해 로그를 아예 안 남기거나, 간단히 한 줄만 남깁니다.
        log.warn("Page Not Found: ${e.requestURL} (${e.httpMethod})")

        return ApiResponse.error(CoreApiErrorType.NOT_FOUND)
    }

    @ExceptionHandler(CoreApiException::class)
    fun handleCoreApiException(e: CoreApiException): ResponseEntity<ApiResponse<Any>> {
        when (e.coreApiErrorType.logLevel) {
            LogLevel.ERROR -> log.error("CoreApiException : {}", e.message, e)
            LogLevel.WARN -> log.warn("CoreApiException : {}", e.message, e)
            else -> log.info("CoreApiException : {}", e.message, e)
        }
        return ResponseEntity(ApiResponse.error(e.coreApiErrorType, e.data), e.coreApiErrorType.status)
    }

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(e: CoreException): ResponseEntity<ApiResponse<Any>> {
        val errorType = e.errorType
        logError(errorType, e)

        val status = mapKindToHttpStatus(errorType.kind)
        return ResponseEntity(ApiResponse.error(errorType), status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Any>> {
        log.error("Exception : {}", e.message, e)
        return ResponseEntity(ApiResponse.error(CoreApiErrorType.DEFAULT_ERROR), CoreApiErrorType.DEFAULT_ERROR.status)
    }

    private fun logError(
        errorType: CoreErrorType,
        e: Exception,
    ) {
        when (errorType.level) {
            CoreErrorLevel.ERROR -> log.error("CoreException : {}", e.message, e)
            CoreErrorLevel.WARN -> log.warn("CoreException : {}", e.message, e)
            else -> log.info("CoreException : {}", e.message, e)
        }
    }

    /**
     * 도메인 에러의 Kind를 HTTP Status로 번역
     */
    private fun mapKindToHttpStatus(kind: CoreErrorKind): HttpStatus =
        when (kind) {
            CoreErrorKind.CLIENT_ERROR -> HttpStatus.BAD_REQUEST
            CoreErrorKind.SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
            CoreErrorKind.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
        }
}
