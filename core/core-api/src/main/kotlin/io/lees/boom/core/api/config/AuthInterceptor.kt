package io.lees.boom.core.api.config

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.support.jwt.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
@Component
class AuthInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
) : HandlerInterceptor {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val USER_ID_ATTRIBUTE = "userId"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (HttpMethod.OPTIONS.matches(request.method)) {
            return true
        }

        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith(BEARER_PREFIX)) {
            throw CoreException(CoreErrorType.UNAUTHORIZED_USER)
        }

        val token = authHeader.removePrefix(BEARER_PREFIX)
        val claims = jwtTokenProvider.validateAndGetClaims(token)

        val userId = claims.subject?.toLongOrNull()
            ?: throw CoreException(CoreErrorType.INVALID_USERID)

        request.setAttribute(USER_ID_ATTRIBUTE, userId)
        return true
    }
}
