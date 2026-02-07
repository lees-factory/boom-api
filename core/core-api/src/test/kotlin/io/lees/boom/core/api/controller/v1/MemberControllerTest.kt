package io.lees.boom.core.api.controller.v1

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.api.controller.v1.request.MemberLoginRequest
import io.lees.boom.core.domain.BadgeResult
import io.lees.boom.core.domain.BadgeService
import io.lees.boom.core.domain.Member
import io.lees.boom.core.domain.MemberService
import io.lees.boom.core.domain.SocialInfo
import io.lees.boom.core.domain.SocialLoginService
import io.lees.boom.core.domain.TokenPair
import io.lees.boom.core.enums.BadgeType
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
import java.time.LocalDateTime

class MemberControllerTest : RestDocsTest() {
    private lateinit var socialLoginService: SocialLoginService
    private lateinit var memberService: MemberService
    private lateinit var badgeService: BadgeService

    @BeforeEach
    fun setUp() {
        socialLoginService = mockk()
        memberService = mockk()
        badgeService = mockk()
        mockMvc =
            mockController(
                MemberController(socialLoginService, memberService, badgeService),
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
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일").optional(),
                        fieldWithPath(
                            "data.profileImage",
                        ).type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getMemberProfile() {
        // given
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
        val now = LocalDateTime.of(2026, 2, 8, 14, 0)
        val badges =
            listOf(
                BadgeResult(BadgeType.ATTEND_10, isEarned = true, isNew = false, acquiredAt = now.minusDays(30)),
                BadgeResult(BadgeType.ATTEND_30, isEarned = true, isNew = false, acquiredAt = now.minusDays(10)),
                BadgeResult(BadgeType.ATTEND_100, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.ATTEND_300, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.ATTEND_999, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.STREAK_7, isEarned = true, isNew = false, acquiredAt = now.minusDays(5)),
                BadgeResult(BadgeType.STREAK_30, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.WEEK_3, isEarned = true, isNew = false, acquiredAt = now.minusDays(20)),
                BadgeResult(BadgeType.WEEK_5, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.WEEK_7, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.EARLY, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.NIGHT, isEarned = true, isNew = false, acquiredAt = now.minusDays(3)),
                BadgeResult(BadgeType.OPENER, isEarned = false, isNew = false, acquiredAt = null),
                BadgeResult(BadgeType.WANDERER, isEarned = false, isNew = false, acquiredAt = null),
            )

        every { memberService.getMember(targetMemberId) } returns member
        every { badgeService.getMemberBadges(targetMemberId) } returns badges

        // when & then
        mockMvc
            .perform(
                get("/api/v1/members/{memberId}", targetMemberId),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getMemberProfile",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("memberId").description("조회할 유저 ID"),
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
                        fieldWithPath("data.badges[].badgeType").type(JsonFieldType.STRING).description("뱃지 코드"),
                        fieldWithPath("data.badges[].category").type(JsonFieldType.STRING).description("뱃지 카테고리"),
                        fieldWithPath("data.badges[].title").type(JsonFieldType.STRING).description("뱃지 이름"),
                        fieldWithPath("data.badges[].emoji").type(JsonFieldType.STRING).description("뱃지 이모지"),
                        fieldWithPath(
                            "data.badges[].description",
                        ).type(JsonFieldType.STRING).description("뱃지 달성 조건 설명"),
                        fieldWithPath("data.badges[].isEarned").type(JsonFieldType.BOOLEAN).description("획득 여부"),
                        fieldWithPath(
                            "data.badges[].isNew",
                        ).type(JsonFieldType.BOOLEAN).description("신규 획득 여부 (타유저 조회 시 항상 false)"),
                        fieldWithPath(
                            "data.badges[].acquiredAt",
                        ).type(JsonFieldType.STRING).description("획득 일시").optional(),
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
