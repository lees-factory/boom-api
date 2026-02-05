package io.lees.boom.core.api.config

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.core.support.User
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import kotlin.jvm.java

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(User::class.java) &&
            (parameter.parameterType == Long::class.java || parameter.parameterType == Long::class.javaObjectType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Long {
        // Interceptor에서 "userId"라는 키로 setAttribute 해둔 값을 꺼냅니다.
        val userId = webRequest.getAttribute("userId", RequestAttributes.SCOPE_REQUEST) as? Long

        // 값이 없으면 인터셉터를 통과하지 않았거나 인증 실패로 간주
        return userId ?: throw CoreException(CoreErrorType.UNAUTHORIZED_USER)
    }
}
