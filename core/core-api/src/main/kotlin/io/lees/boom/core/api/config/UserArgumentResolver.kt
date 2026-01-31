package io.lees.boom.core.api.config

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.core.support.User
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import kotlin.jvm.java

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        // 1. @User 어노테이션 존재 확인
        if (!parameter.hasParameterAnnotation(User::class.java)) {
            return false
        }

        // 2. 타입 확인: Primitive long과 Wrapper Long 모두 지원
        val type = parameter.parameterType
        return type == Long::class.java || type == Long::class.javaObjectType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Long {
        val userIdHeader = webRequest.getHeader("X-User-Id")

        if (userIdHeader.isNullOrBlank()) {
            throw CoreException(CoreErrorType.UNAUTHORIZED_USER)
        }

        return try {
            userIdHeader.toLong()
        } catch (e: NumberFormatException) {
            throw CoreException(CoreErrorType.INVALID_USERID)
        }
    }
}
