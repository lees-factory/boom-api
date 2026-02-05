package io.lees.boom.core.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class CoreApiErrorType(
    val status: HttpStatus,
    val code: CoreApiErrorCode,
    val message: String,
    val logLevel: LogLevel,
) {
    DEFAULT_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        CoreApiErrorCode.E500,
        "An unexpected error has occurred.",
        LogLevel.ERROR,
    ),

    NOT_FOUND(HttpStatus.NOT_FOUND, CoreApiErrorCode.E501, "Not Found", LogLevel.INFO),
}
