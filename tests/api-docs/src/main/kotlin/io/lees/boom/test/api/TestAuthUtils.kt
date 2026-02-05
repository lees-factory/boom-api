package io.lees.boom.test.api

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.request.RequestPostProcessor

/**
 * 테스트에서 인증된 사용자를 시뮬레이션하기 위한 유틸리티
 *
 * Production에서는 AuthInterceptor가 JWT 토큰을 검증하고 userId를 request attribute에 설정합니다.
 * 테스트에서는 이 RequestPostProcessor를 사용하여 동일한 동작을 시뮬레이션합니다.
 */
object TestAuthUtils {
    private const val USER_ID_ATTRIBUTE = "userId"
    private const val AUTHORIZATION_HEADER = "Authorization"
    private const val MOCK_TOKEN = "Bearer test-access-token"

    /**
     * 인증된 사용자로 요청을 수행하기 위한 RequestPostProcessor
     *
     * @param userId 인증된 사용자 ID
     * @return RequestPostProcessor
     *
     * 사용 예시:
     * ```
     * mockMvc.perform(
     *     post("/api/v1/gyms/{gymId}/entry", gymId)
     *         .with(authenticatedUser(1L))
     *         .contentType(MediaType.APPLICATION_JSON)
     * )
     * ```
     */
    fun authenticatedUser(userId: Long): RequestPostProcessor =
        RequestPostProcessor { request: MockHttpServletRequest ->
            // Interceptor가 설정하는 것처럼 userId attribute 설정
            request.setAttribute(USER_ID_ATTRIBUTE, userId)
            // RestDocs 문서화를 위해 Authorization 헤더도 추가
            request.addHeader(AUTHORIZATION_HEADER, MOCK_TOKEN)
            request
        }
}
