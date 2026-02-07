package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.domain.BadgeResult
import io.lees.boom.core.domain.BadgeService
import io.lees.boom.core.enums.BadgeType
import io.lees.boom.test.api.RestDocsTest
import io.lees.boom.test.api.TestAuthUtils.authenticatedUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class BadgeControllerTest : RestDocsTest() {
    private lateinit var badgeService: BadgeService

    @BeforeEach
    fun setUp() {
        badgeService = mockk()
        mockMvc =
            mockController(
                BadgeController(badgeService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun getMyBadges() {
        // given
        val memberId = 1L
        val now = LocalDateTime.of(2026, 2, 8, 14, 0)
        val badges =
            listOf(
                BadgeResult(
                    badgeType = BadgeType.ATTEND_10,
                    isEarned = true,
                    isNew = false,
                    acquiredAt = now.minusDays(30),
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_30,
                    isEarned = true,
                    isNew = true,
                    acquiredAt = now,
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_100,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_300,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_999,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.STREAK_7,
                    isEarned = true,
                    isNew = false,
                    acquiredAt = now.minusDays(10),
                ),
                BadgeResult(
                    badgeType = BadgeType.STREAK_30,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.WEEK_3,
                    isEarned = true,
                    isNew = false,
                    acquiredAt = now.minusDays(20),
                ),
                BadgeResult(
                    badgeType = BadgeType.WEEK_5,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.WEEK_7,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.EARLY,
                    isEarned = true,
                    isNew = true,
                    acquiredAt = now,
                ),
                BadgeResult(
                    badgeType = BadgeType.NIGHT,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.OPENER,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.WANDERER,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
            )

        every { badgeService.getMyBadges(any()) } returns badges

        // when & then
        mockMvc
            .perform(
                get("/api/v1/members/me/badges")
                    .with(authenticatedUser(memberId)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getMyBadges",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/ERROR)"),
                        fieldWithPath(
                            "data[].badgeType",
                        ).type(JsonFieldType.STRING).description("뱃지 코드 (ATTEND_10, STREAK_7 등)"),
                        fieldWithPath(
                            "data[].category",
                        ).type(
                            JsonFieldType.STRING,
                        ).description("뱃지 카테고리 (ATTENDANCE/STREAK/WEEKLY/TIME/VISIT_PATTERN)"),
                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("뱃지 이름"),
                        fieldWithPath("data[].emoji").type(JsonFieldType.STRING).description("뱃지 이모지"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("뱃지 달성 조건 설명"),
                        fieldWithPath("data[].isEarned").type(JsonFieldType.BOOLEAN).description("획득 여부"),
                        fieldWithPath("data[].isNew").type(JsonFieldType.BOOLEAN).description("이번 조회에서 신규 획득 여부"),
                        fieldWithPath("data[].acquiredAt").type(JsonFieldType.STRING).description("획득 일시").optional(),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getMemberBadges() {
        // given
        val targetMemberId = 2L
        val now = LocalDateTime.of(2026, 2, 8, 14, 0)
        val badges =
            listOf(
                BadgeResult(
                    badgeType = BadgeType.ATTEND_10,
                    isEarned = true,
                    isNew = false,
                    acquiredAt = now.minusDays(30),
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_30,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_100,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_300,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.ATTEND_999,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.STREAK_7,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.STREAK_30,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.WEEK_3,
                    isEarned = true,
                    isNew = false,
                    acquiredAt = now.minusDays(15),
                ),
                BadgeResult(
                    badgeType = BadgeType.WEEK_5,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.WEEK_7,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.EARLY,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.NIGHT,
                    isEarned = true,
                    isNew = false,
                    acquiredAt = now.minusDays(5),
                ),
                BadgeResult(
                    badgeType = BadgeType.OPENER,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
                BadgeResult(
                    badgeType = BadgeType.WANDERER,
                    isEarned = false,
                    isNew = false,
                    acquiredAt = null,
                ),
            )

        every { badgeService.getMemberBadges(any()) } returns badges

        // when & then
        mockMvc
            .perform(
                get("/api/v1/members/{memberId}/badges", targetMemberId),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getMemberBadges",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("memberId").description("조회할 유저 ID"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/ERROR)"),
                        fieldWithPath("data[].badgeType").type(JsonFieldType.STRING).description("뱃지 코드"),
                        fieldWithPath("data[].category").type(JsonFieldType.STRING).description("뱃지 카테고리"),
                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("뱃지 이름"),
                        fieldWithPath("data[].emoji").type(JsonFieldType.STRING).description("뱃지 이모지"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("뱃지 달성 조건 설명"),
                        fieldWithPath("data[].isEarned").type(JsonFieldType.BOOLEAN).description("획득 여부"),
                        fieldWithPath(
                            "data[].isNew",
                        ).type(JsonFieldType.BOOLEAN).description("신규 획득 여부 (타유저 조회 시 항상 false)"),
                        fieldWithPath("data[].acquiredAt").type(JsonFieldType.STRING).description("획득 일시").optional(),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }
}
