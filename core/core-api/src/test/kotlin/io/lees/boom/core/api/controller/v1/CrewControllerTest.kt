package io.lees.boom.core.api.controller.v1

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.api.controller.v1.request.CrewCreateRequest
import io.lees.boom.core.domain.Crew
import io.lees.boom.core.domain.CrewService
import io.lees.boom.test.api.RestDocsTest
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
        // UserArgumentResolver를 명시적으로 등록해야 @User 어노테이션이 동작함
        mockMvc =
            mockController(
                CrewController(crewService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun createCrew() {
        // given
        val request = CrewCreateRequest(name = "클라이밍 붐", description = "부산 클라이밍 크루입니다.", maxMemberCount = 100)
        val createdCrew = Crew(id = 1L, name = request.name, description = request.description, maxMemberCount = 100)

        // Service 파라미터 4개에 대응
        every { crewService.createCrew(any(), any(), any(), any()) } returns createdCrew

        // when & then
        mockMvc
            .perform(
                post("/api/v1/crews")
                    .header("X-User-Id", "1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper().writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "createCrew",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-Id").description("로그인 사용자 ID"),
                    ),
                    requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("크루 이름"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("크루 설명"),
                        fieldWithPath(
                            "maxMemberCount",
                        ).type(JsonFieldType.NUMBER).description("크루 최대 인원수 (기본 100명)").optional(), // [추가]
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
        justRun { crewService.joinCrew(any(), any()) }

        // when & then
        mockMvc
            .perform(
                post("/api/v1/crews/{crewId}/join", 1L)
                    .header("X-User-Id", "2") // Resolver 동작 확인
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "joinCrew",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-Id").description("로그인 사용자 ID"),
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
