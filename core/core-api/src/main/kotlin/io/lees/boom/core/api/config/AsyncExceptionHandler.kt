package io.lees.boom.core.api.config

import io.lees.boom.core.error.CoreErrorLevel
import io.lees.boom.core.error.CoreException

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler

import java.lang.reflect.Method

class AsyncExceptionHandler : AsyncUncaughtExceptionHandler {

    private val log = LoggerFactory.getLogger(AsyncExceptionHandler::class.java)

    override fun handleUncaughtException(e: Throwable, method: Method, vararg params: Any?) {
        if (e is CoreException) {
            when (e.errorType.level) {
                CoreErrorLevel.ERROR -> log.error("CoreException : {}", e.message, e)
                CoreErrorLevel.WARN -> log.warn("CoreException : {}", e.message, e)
                else -> log.info("CoreException : {}", e.message, e)
            }
        } else {
            log.error("Exception : {}", e.message, e)
        }
    }
}