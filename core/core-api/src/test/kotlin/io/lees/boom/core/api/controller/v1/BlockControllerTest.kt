package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.domain.member.MemberBlockService
import io.lees.boom.test.api.RestDocsTest
import io.lees.boom.test.api.TestAuthUtils.authenticatedUser
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BlockControllerTest : RestDocsTest() {
    private lateinit var memberBlockService: MemberBlockService

    @BeforeEach
    fun setUp() {
        memberBlockService = mockk()
        mockMvc =
            mockController(
                BlockController(memberBlockService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun blockMember() {
        // given
        val memberId = 1L
        val targetMemberId = 2L

        justRun { memberBlockService.block(any(), any()) }

        // when & then
        mockMvc
            .perform(
                post("/api/v1/members/{targetMemberId}/block", targetMemberId)
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "blockMember",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("targetMemberId").description("차단할 유저 ID"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data").type(JsonFieldType.NULL).ignored(),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun unblockMember() {
        // given
        val memberId = 1L
        val targetMemberId = 2L

        justRun { memberBlockService.unblock(any(), any()) }

        // when & then
        mockMvc
            .perform(
                delete("/api/v1/members/{targetMemberId}/block", targetMemberId)
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "unblockMember",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("targetMemberId").description("차단 해제할 유저 ID"),
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
