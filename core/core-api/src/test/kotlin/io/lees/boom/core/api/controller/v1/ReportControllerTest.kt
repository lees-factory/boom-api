package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.domain.report.Report
import io.lees.boom.core.domain.report.ReportService
import io.lees.boom.core.enums.ReportReason
import io.lees.boom.core.enums.ReportTargetType
import io.lees.boom.test.api.RestDocsTest
import io.lees.boom.test.api.TestAuthUtils.authenticatedUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ReportControllerTest : RestDocsTest() {
    private lateinit var reportService: ReportService

    @BeforeEach
    fun setUp() {
        reportService = mockk()
        mockMvc =
            mockController(
                ReportController(reportService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun createReport() {
        // given
        val memberId = 1L
        val requestBody =
            """
            {
                "targetType": "MEMBER",
                "targetId": 2,
                "reason": "ABUSE_HARASSMENT",
                "description": "욕설을 합니다."
            }
            """.trimIndent()

        every { reportService.createReport(any(), any(), any(), any(), any()) } returns
            Report(
                id = 1L,
                reporterId = memberId,
                targetType = ReportTargetType.MEMBER,
                targetId = 2L,
                reason = ReportReason.ABUSE_HARASSMENT,
                description = "욕설을 합니다.",
            )

        // when & then
        mockMvc
            .perform(
                post("/api/v1/reports")
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "createReport",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    requestFields(
                        fieldWithPath(
                            "targetType",
                        ).type(JsonFieldType.STRING).description("신고 대상 타입 (MEMBER, CHAT_MESSAGE)"),
                        fieldWithPath("targetId").type(JsonFieldType.NUMBER).description("신고 대상 ID"),
                        fieldWithPath(
                            "reason",
                        ).type(JsonFieldType.STRING).description(
                            "신고 사유 (ABUSE_HARASSMENT, SPAM_COMMERCIAL, INAPPROPRIATE_CONTENT)",
                        ),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("상세 설명").optional(),
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
