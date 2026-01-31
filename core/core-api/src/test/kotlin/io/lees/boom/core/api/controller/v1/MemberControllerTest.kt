package io.lees.boom.core.api.controller.v1

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.lees.boom.core.api.controller.v1.request.MemberLoginRequest
import io.lees.boom.core.domain.Member
import io.lees.boom.core.domain.SocialInfo
import io.lees.boom.core.domain.SocialLoginService
import io.lees.boom.core.enums.MemberRole
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
        val request = MemberLoginRequest(provider = SocialProvider.KAKAO, token = "sample-token")
        val member =
            Member(
                id = 1L,
                name = "홍길동",
                email = "test@test.com",
                role = MemberRole.USER,
                socialInfo = SocialInfo(provider = SocialProvider.KAKAO, socialId = "social-id-123"),
            )

        every { socialLoginService.login(any(), any()) } returns member

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
                        ).type(JsonFieldType.STRING).description("소셜 로그인 제공자 (GOOGLE, APPLE, KAKAO)"),
                        fieldWithPath("token").type(JsonFieldType.STRING).description("소셜 서비스에서 발급받은 토큰"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/ERROR)"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 고유 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자 이름"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 권한"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }
}
