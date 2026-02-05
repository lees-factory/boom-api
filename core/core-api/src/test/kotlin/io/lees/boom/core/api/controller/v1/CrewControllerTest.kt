package io.lees.boom.core.api.controller.v1

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.api.controller.v1.request.CrewCreateRequest
import io.lees.boom.core.domain.Crew
import io.lees.boom.core.domain.CrewService
import io.lees.boom.test.api.RestDocsTest
import io.lees.boom.test.api.TestAuthUtils.authenticatedUser
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CrewControllerTest : RestDocsTest() {
    private lateinit var crewService: CrewService

    @BeforeEach
    fun setUp() {
        crewService = mockk()
        mockMvc =
            mockController(
                CrewController(crewService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun createCrew() {
        // given
        val memberId = 1L
        val request = CrewCreateRequest(name = "클라이밍 붐", description = "부산 클라이밍 크루입니다.", maxMemberCount = 100)
        val createdCrew = Crew(id = 1L, name = request.name, description = request.description, maxMemberCount = 100)

        every { crewService.createCrew(any(), any(), any(), any()) } returns createdCrew

        // when & then
        mockMvc
            .perform(
                post("/api/v1/crews")
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper().writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "createCrew",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("크루 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("크루 설명"),
                        fieldWithPath(
                            "maxMemberCount",
                        ).type(JsonFieldType.NUMBER).description("크루 최대 인원수 (기본 100명)").optional(),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/ERROR)"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 크루 ID"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun joinCrew() {
        // given
        val memberId = 2L
        justRun { crewService.joinCrew(any(), any()) }

        // when & then
        mockMvc
            .perform(
                post("/api/v1/crews/{crewId}/join", 1L)
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "joinCrew",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("crewId").description("가입할 크루 ID"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data").type(JsonFieldType.NULL).ignored(),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }
}
