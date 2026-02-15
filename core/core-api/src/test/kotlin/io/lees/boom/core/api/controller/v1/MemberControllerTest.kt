package io.lees.boom.core.api.controller.v1

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.api.controller.v1.request.MemberLoginRequest
import io.lees.boom.core.domain.auth.TokenPair
import io.lees.boom.core.domain.member.Member
import io.lees.boom.core.domain.member.MemberBlockService
import io.lees.boom.core.domain.member.MemberService
import io.lees.boom.core.domain.member.SocialInfo
import io.lees.boom.core.domain.member.SocialLoginService
import io.lees.boom.core.enums.MemberRole
import io.lees.boom.core.enums.SocialProvider
import io.lees.boom.test.api.RestDocsTest
import io.lees.boom.test.api.TestAuthUtils.authenticatedUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerTest : RestDocsTest() {
    private lateinit var socialLoginService: SocialLoginService
    private lateinit var memberService: MemberService
    private lateinit var memberBlockService: MemberBlockService

    @BeforeEach
    fun setUp() {
        socialLoginService = mockk()
        memberService = mockk()
        memberBlockService = mockk()
        mockMvc =
            mockController(
                MemberController(socialLoginService, memberService, memberBlockService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun login() {
        // given
        val request =
            MemberLoginRequest(
                provider = SocialProvider.KAKAO,
                socialId = "social-id-123",
                name = "홍길동",
                email = "test@test.com",
                profileImage = "http://k.kakaocdn.net/image.jpg",
            )

        val tokenPair =
            TokenPair(
                accessToken = "access-token-sample",
                refreshToken = "refresh-token-sample",
            )

        every {
            socialLoginService.login(any(), any(), any(), any(), any())
        } returns tokenPair

        // when & then
        mockMvc
            .perform(
                post("/api/v1/members/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper().writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "member-login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath(
                            "provider",
                        ).type(JsonFieldType.STRING).description("소셜 로그인 제공자 (KAKAO, APPLE, GOOGLE)"),
                        fieldWithPath("socialId").type(JsonFieldType.STRING).description("소셜 서비스의 고유 ID"),
                        fieldWithPath(
                            "name",
                        ).type(
                            JsonFieldType.STRING,
                        ).description("사용자 이름(닉네임) - Apple은 null 가능, null이면 UUID 기반 이름 자동 생성")
                            .optional(),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일 (선택)").optional(),
                        fieldWithPath(
                            "profileImage",
                        ).type(JsonFieldType.STRING).description("프로필 이미지 URL (선택)").optional(),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS)"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("Access Token (1시간)"),
                        fieldWithPath(
                            "data.refreshToken",
                        ).type(JsonFieldType.STRING).description("Refresh Token (30일)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun refresh() {
        // given
        val request =
            mapOf("refreshToken" to "old-refresh-token-sample")

        val tokenPair =
            TokenPair(
                accessToken = "new-access-token-sample",
                refreshToken = "new-refresh-token-sample",
            )

        every {
            socialLoginService.refreshToken(any())
        } returns tokenPair

        // when & then
        mockMvc
            .perform(
                post("/api/v1/members/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper().writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "member-refresh",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("기존 Refresh Token"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS)"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("새로운 Access Token"),
                        fieldWithPath(
                            "data.refreshToken",
                        ).type(JsonFieldType.STRING).description("새로운 Refresh Token (Rotation)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getMe() {
        // given
        val memberId = 1L
        val member =
            Member(
                id = memberId,
                name = "홍길동",
                email = "test@test.com",
                profileImage = "https://example.com/profile.jpg",
                role = MemberRole.USER,
                socialInfo = SocialInfo(SocialProvider.KAKAO, "social-id-123"),
                activityScore = 120,
            )

        every { memberService.getMe(memberId) } returns member

        // when & then
        mockMvc
            .perform(
                get("/api/v1/members/me")
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getMe",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("회원 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath(
                            "data.profileImage",
                        ).type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                        fieldWithPath("data.activityScore").type(JsonFieldType.NUMBER).description("활동 점수"),
                        fieldWithPath(
                            "data.activityRank",
                        ).type(JsonFieldType.STRING).description("활동 등급 (노랑단/초록단/빨강단/보라단/황금단)"),
                        fieldWithPath(
                            "data.activityRankColor",
                        ).type(JsonFieldType.STRING).description("활동 등급 컬러 코드 (HEX)"),
                        fieldWithPath(
                            "data.isBlocked",
                        ).type(JsonFieldType.BOOLEAN).description("차단 여부 (내 정보 조회 시 항상 false)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getMemberProfile() {
        // given
        val memberId = 1L
        val targetMemberId = 2L
        val member =
            Member(
                id = targetMemberId,
                name = "김철수",
                email = "kim@test.com",
                profileImage = "https://example.com/profile2.jpg",
                role = MemberRole.USER,
                socialInfo = SocialInfo(SocialProvider.KAKAO, "social-id-456"),
                activityScore = 150,
            )

        every { memberService.getMember(targetMemberId) } returns member
        every { memberBlockService.isBlocked(memberId, targetMemberId) } returns false

        // when & then
        mockMvc
            .perform(
                get("/api/v1/members/{targetMemberId}", targetMemberId)
                    .with(authenticatedUser(memberId)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getMemberProfile",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("targetMemberId").description("조회할 유저 ID"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("회원 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath(
                            "data.profileImage",
                        ).type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                        fieldWithPath("data.activityScore").type(JsonFieldType.NUMBER).description("활동 점수"),
                        fieldWithPath(
                            "data.activityRank",
                        ).type(JsonFieldType.STRING).description("활동 등급 (노랑단/초록단/빨강단/보라단/황금단)"),
                        fieldWithPath(
                            "data.activityRankColor",
                        ).type(JsonFieldType.STRING).description("활동 등급 컬러 코드 (HEX)"),
                        fieldWithPath(
                            "data.isBlocked",
                        ).type(JsonFieldType.BOOLEAN).description("내가 해당 유저를 차단했는지 여부"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun updateMe() {
        // given
        val memberId = 1L
        val updatedMember =
            Member(
                id = memberId,
                name = "새닉네임",
                email = "new@test.com",
                profileImage = "https://supabase.co/storage/v1/object/public/boom/profile/1/abc.jpg",
                role = MemberRole.USER,
                socialInfo = SocialInfo(SocialProvider.KAKAO, "social-id-123"),
            )

        every { memberService.updateMe(memberId, any(), any(), any()) } returns updatedMember

        val profileImage =
            MockMultipartFile(
                "profileImage",
                "profile.jpg",
                "image/jpeg",
                "fake-image-content".toByteArray(),
            )
        val namePart =
            MockMultipartFile(
                "name",
                "",
                "text/plain",
                "새닉네임".toByteArray(),
            )
        val emailPart =
            MockMultipartFile(
                "email",
                "",
                "text/plain",
                "new@test.com".toByteArray(),
            )

        // when & then
        mockMvc
            .perform(
                multipart("/api/v1/members/me")
                    .file(profileImage)
                    .file(namePart)
                    .file(emailPart)
                    .with(authenticatedUser(memberId))
                    .with { request ->
                        request.method = "PUT"
                        request
                    },
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "updateMe",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("회원 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("닉네임"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일").optional(),
                        fieldWithPath(
                            "data.profileImage",
                        ).type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }
}
