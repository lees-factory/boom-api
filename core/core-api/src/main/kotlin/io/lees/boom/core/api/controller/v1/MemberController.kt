package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.MemberLoginRequest
import io.lees.boom.core.api.controller.v1.response.MemberResponse
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
     * [POST] /api/v1/members/login
     * * 클라이언트 SDK에서 받은 토큰을 서버로 전달하여 검증 및 로그인 처리
     */
    @PostMapping("/login")
    fun login(
        @RequestBody request: MemberLoginRequest,
    ): ApiResponse<MemberResponse> {
        // [Coordinator] 비즈니스 로직(Service)에 흐름 위임
        val member = socialLoginService.login(request.provider, request.token)

        // [Presentation] 도메인 객체를 응답 DTO로 변환하여 반환
        return ApiResponse.success(MemberResponse.from(member))
    }
}
