package io.lees.boom.core.api.controller.v1

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.lees.boom.core.api.controller.v1.request.MemberLoginRequest
import io.lees.boom.core.domain.SocialLoginService
import io.lees.boom.core.domain.TokenPair
import io.lees.boom.core.enums.SocialProvider
import io.lees.boom.test.api.RestDocsTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerTest : RestDocsTest() {
    private lateinit var socialLoginService: SocialLoginService

    @BeforeEach
    fun setUp() {
        socialLoginService = mockk()
        mockMvc = mockController(MemberController(socialLoginService))
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
                        fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름(닉네임)"),
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
}
