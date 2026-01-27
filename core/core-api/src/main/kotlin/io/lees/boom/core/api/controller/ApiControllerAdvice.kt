package io.lees.boom.core.api.controller

import io.lees.boom.core.support.error.CoreApiException
import io.lees.boom.core.support.error.CoreApiErrorType
import io.lees.boom.core.support.response.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CoreApiException::class)
    fun handleCoreException(e: CoreApiException): ResponseEntity<ApiResponse<Any>> {
        when (e.coreApiErrorType.logLevel) {
            LogLevel.ERROR -> log.error("CoreException : {}", e.message, e)
            LogLevel.WARN -> log.warn("CoreException : {}", e.message, e)
            else -> log.info("CoreException : {}", e.message, e)
        }
        return ResponseEntity(ApiResponse.error(e.coreApiErrorType, e.data), e.coreApiErrorType.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Any>> {
        log.error("Exception : {}", e.message, e)
        return ResponseEntity(ApiResponse.error(CoreApiErrorType.DEFAULT_ERROR), CoreApiErrorType.DEFAULT_ERROR.status)
    }
}
