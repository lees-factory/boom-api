package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.domain.CrewChatMessage
import io.lees.boom.core.domain.CrewChatMessageInfo
import io.lees.boom.core.domain.CrewChatService
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class CrewChatControllerTest : RestDocsTest() {
    private lateinit var crewChatService: CrewChatService

    @BeforeEach
    fun setUp() {
        crewChatService = mockk()
        mockMvc =
            mockController(
                CrewChatController(crewChatService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun sendMessage() {
        // given
        val memberId = 1L
        val crewId = 1L
        val message =
            CrewChatMessage(
                id = 100L,
                crewId = crewId,
                memberId = memberId,
                content = "안녕하세요! 오늘 클라이밍 가실 분?",
                createdAt = LocalDateTime.of(2026, 2, 10, 14, 30),
            )

        every { crewChatService.sendMessage(any(), any(), any()) } returns message

        val requestBody =
            """
            {
                "content": "안녕하세요! 오늘 클라이밍 가실 분?"
            }
            """.trimIndent()

        // when & then
        mockMvc
            .perform(
                post("/api/v1/crews/{crewId}/chat", crewId)
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "sendChatMessage",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("crewId").description("크루 ID"),
                    ),
                    requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("메시지 내용"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data.messageId").type(JsonFieldType.NUMBER).description("생성된 메시지 ID"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getMessages() {
        // given
        val memberId = 1L
        val crewId = 1L
        val messages =
            listOf(
                CrewChatMessageInfo(
                    messageId = 100L,
                    memberId = 1L,
                    memberName = "홍길동",
                    memberProfileImage = "https://example.com/profile1.jpg",
                    content = "안녕하세요! 오늘 클라이밍 가실 분?",
                    createdAt = LocalDateTime.of(2026, 2, 10, 14, 30),
                ),
                CrewChatMessageInfo(
                    messageId = 99L,
                    memberId = 2L,
                    memberName = "김철수",
                    memberProfileImage = null,
                    content = "저요! 몇 시에 만날까요?",
                    createdAt = LocalDateTime.of(2026, 2, 10, 14, 28),
                ),
            )

        every { crewChatService.getMessages(any(), any(), any(), any()) } returns messages

        // when & then
        mockMvc
            .perform(
                get("/api/v1/crews/{crewId}/chat", crewId)
                    .param("cursor", "101")
                    .param("size", "10")
                    .with(authenticatedUser(memberId)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getChatMessages",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("crewId").description("크루 ID"),
                    ),
                    queryParameters(
                        parameterWithName("cursor").description("커서 (마지막 메시지 ID, 첫 조회 시 생략)").optional(),
                        parameterWithName("size").description("조회 개수 (기본 10)").optional(),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].messageId").type(JsonFieldType.NUMBER).description("메시지 ID"),
                        fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("작성자 멤버 ID"),
                        fieldWithPath("data[].memberName").type(JsonFieldType.STRING).description("작성자 이름"),
                        fieldWithPath(
                            "data[].memberProfileImage",
                        ).type(JsonFieldType.STRING).description("작성자 프로필 이미지 URL").optional(),
                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("메시지 내용"),
                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("전송 일시"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun deleteMessage() {
        // given
        val memberId = 1L
        val crewId = 1L
        val messageId = 100L

        justRun { crewChatService.deleteMessage(any(), any(), any()) }

        // when & then
        mockMvc
            .perform(
                delete("/api/v1/crews/{crewId}/chat/{messageId}", crewId, messageId)
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "deleteChatMessage",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("crewId").description("크루 ID"),
                        parameterWithName("messageId").description("삭제할 메시지 ID"),
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
