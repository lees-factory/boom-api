package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.domain.member.MemberNotificationSetting
import io.lees.boom.core.domain.member.NotificationSettingService
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class NotificationSettingControllerTest : RestDocsTest() {
    private lateinit var notificationSettingService: NotificationSettingService

    @BeforeEach
    fun setUp() {
        notificationSettingService = mockk()
        mockMvc =
            mockController(
                NotificationSettingController(notificationSettingService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun getNotificationSetting() {
        // given
        val memberId = 1L
        val setting =
            MemberNotificationSetting(
                id = 1L,
                memberId = memberId,
                pushToken = "ExponentPushToken[xxxx]",
                pushEnabled = true,
                crewScheduleEnabled = true,
                crewChatEnabled = false,
            )

        every { notificationSettingService.getOrCreateSetting(any()) } returns setting

        // when & then
        mockMvc
            .perform(
                get("/api/v1/members/me/notification-setting")
                    .with(authenticatedUser(memberId)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getNotificationSetting",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/ERROR)"),
                        fieldWithPath("data.pushEnabled").type(JsonFieldType.BOOLEAN).description("푸시 알림 글로벌 on/off"),
                        fieldWithPath(
                            "data.crewScheduleEnabled",
                        ).type(JsonFieldType.BOOLEAN).description("크루 일정 알림 on/off"),
                        fieldWithPath(
                            "data.crewChatEnabled",
                        ).type(JsonFieldType.BOOLEAN).description("크루 채팅 알림 on/off"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun updateNotificationSetting() {
        // given
        val memberId = 1L
        justRun { notificationSettingService.updateSetting(any(), any(), any(), any()) }

        // when & then
        mockMvc
            .perform(
                put("/api/v1/members/me/notification-setting")
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "pushEnabled": true,
                            "crewScheduleEnabled": false,
                            "crewChatEnabled": true
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "updateNotificationSetting",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    requestFields(
                        fieldWithPath("pushEnabled").type(JsonFieldType.BOOLEAN).description("푸시 알림 글로벌 on/off"),
                        fieldWithPath("crewScheduleEnabled").type(JsonFieldType.BOOLEAN).description("크루 일정 알림 on/off"),
                        fieldWithPath("crewChatEnabled").type(JsonFieldType.BOOLEAN).description("크루 채팅 알림 on/off"),
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
    fun updatePushToken() {
        // given
        val memberId = 1L
        justRun { notificationSettingService.updatePushToken(any(), any()) }

        // when & then
        mockMvc
            .perform(
                put("/api/v1/members/me/push-token")
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                            "pushToken": "ExponentPushToken[xxxx-yyyy]"
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "updatePushToken",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    requestFields(
                        fieldWithPath("pushToken").type(JsonFieldType.STRING).description("Expo 푸시 토큰"),
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
