package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.MemberLoginRequest
import io.lees.boom.core.api.controller.v1.request.TokenRefreshRequest
import io.lees.boom.core.api.controller.v1.response.MemberLoginResponse
import io.lees.boom.core.api.controller.v1.response.MemberProfileResponse
import io.lees.boom.core.api.controller.v1.response.MemberResponse
import io.lees.boom.core.domain.MemberService
import io.lees.boom.core.domain.ProfileImageInput
import io.lees.boom.core.domain.SocialLoginService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val socialLoginService: SocialLoginService,
    private val memberService: MemberService,
) {
    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    fun getMe(
        @User memberId: Long,
    ): ApiResponse<MemberProfileResponse> {
        val member = memberService.getMe(memberId)
        return ApiResponse.success(MemberProfileResponse.of(member))
    }

    /**
     * 타 유저 프로필 조회
     * 암장 입장 유저 목록 등에서 프로필 확인 시 사용
     */
    @GetMapping("/{memberId}")
    fun getMemberProfile(
        @PathVariable memberId: Long,
    ): ApiResponse<MemberProfileResponse> {
        val member = memberService.getMember(memberId)
        return ApiResponse.success(MemberProfileResponse.of(member))
    }

    /**
     * 내 정보 수정
     * nickname, email 은 text로, profileImage는 multipart file로 전송
     */
    @PutMapping("/me")
    fun updateMe(
        @User memberId: Long,
        @RequestPart(required = false) name: String?,
        @RequestPart(required = false) email: String?,
        @RequestPart(required = false) profileImage: MultipartFile?,
    ): ApiResponse<MemberResponse> {
        val imageInput =
            profileImage?.let {
                ProfileImageInput(
                    inputStream = it.inputStream,
                    contentType = it.contentType ?: "image/jpeg",
                    contentLength = it.size,
                )
            }

        val member = memberService.updateMe(memberId, name, email, imageInput)
        return ApiResponse.success(MemberResponse.of(member))
    }

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

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody request: TokenRefreshRequest,
    ): ApiResponse<MemberLoginResponse> {
        val tokenPair = socialLoginService.refreshToken(request.refreshToken)
        return ApiResponse.success(
            MemberLoginResponse(tokenPair.accessToken, tokenPair.refreshToken),
        )
    }
}
