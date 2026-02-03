package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.MemberLoginRequest
import io.lees.boom.core.api.controller.v1.response.MemberLoginResponse
import io.lees.boom.core.domain.SocialLoginService
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val socialLoginService: SocialLoginService,
) {
    /**
     * 소셜 로그인 (및 회원가입)
     * 앱에서 소셜 인증 후 받은 정보를 서버로 전달하여 서비스 전용 토큰(Access/Refresh)을 발급받습니다.
     */
    @PostMapping("/login")
    fun login(
        @RequestBody request: MemberLoginRequest,
    ): ApiResponse<MemberLoginResponse> {
        // 1. 비즈니스 로직 호출 (도메인 객체 TokenPair 반환)
        val tokenPair =
            socialLoginService.login(
                provider = request.provider,
                socialId = request.socialId,
                name = request.name,
                email = request.email,
                profileImage = request.profileImage,
            )

        val response =
            MemberLoginResponse(
                accessToken = tokenPair.accessToken,
                refreshToken = tokenPair.refreshToken,
            )

        // 3. 공통 응답 포맷으로 래핑하여 반환
        return ApiResponse.success(response)
    }
}
