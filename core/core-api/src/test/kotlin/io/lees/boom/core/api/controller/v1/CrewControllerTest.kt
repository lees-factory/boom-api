package io.lees.boom.core.api.controller.v1

import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.api.controller.v1.request.CrewCreateRequest
import io.lees.boom.core.domain.Crew
import io.lees.boom.core.domain.CrewMemberInfo
import io.lees.boom.core.domain.CrewSchedule
import io.lees.boom.core.domain.CrewService
import io.lees.boom.core.domain.MyCrewInfo
import io.lees.boom.core.enums.CrewRole
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
        val request =
            CrewCreateRequest(
                name = "클라이밍 붐",
                description = "부산 클라이밍 크루입니다.",
                maxMemberCount = 100,
                latitude = 35.1796,
                longitude = 129.0756,
                address = "부산광역시 해운대구",
            )
        val createdCrew = Crew(id = 1L, name = request.name, description = request.description, maxMemberCount = 100)

        every { crewService.createCrew(any(), any(), any(), any(), any(), any(), any()) } returns createdCrew

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
                        fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("크루 위치 위도").optional(),
                        fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("크루 위치 경도").optional(),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("크루 주소").optional(),
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
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
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

    @Test
    fun getMyCrews() {
        // given
        val memberId = 1L
        val myCrews =
            listOf(
                MyCrewInfo(
                    crewId = 1L,
                    name = "클라이밍 붐",
                    description = "부산 클라이밍 크루입니다.",
                    maxMemberCount = 100,
                    myRole = CrewRole.LEADER,
                    memberCount = 5,
                ),
                MyCrewInfo(
                    crewId = 2L,
                    name = "볼더링 클럽",
                    description = "볼더링 전문 크루",
                    maxMemberCount = 50,
                    myRole = CrewRole.MEMBER,
                    memberCount = 12,
                ),
            )

        every { crewService.getMyCrews(any()) } returns myCrews

        // when & then
        mockMvc
            .perform(
                get("/api/v1/crews/my")
                    .with(authenticatedUser(memberId)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getMyCrews",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("크루 이름"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("크루 설명"),
                        fieldWithPath("data[].maxMemberCount").type(JsonFieldType.NUMBER).description("최대 인원수"),
                        fieldWithPath(
                            "data[].myRole",
                        ).type(JsonFieldType.STRING).description("내 역할 (LEADER/MEMBER/GUEST)"),
                        fieldWithPath("data[].memberCount").type(JsonFieldType.NUMBER).description("현재 멤버 수"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getCrewMembers() {
        // given
        val crewId = 1L
        val members =
            listOf(
                CrewMemberInfo(
                    memberId = 1L,
                    name = "홍길동",
                    profileImage = "https://example.com/profile1.jpg",
                    role = CrewRole.LEADER,
                ),
                CrewMemberInfo(
                    memberId = 2L,
                    name = "김철수",
                    profileImage = null,
                    role = CrewRole.MEMBER,
                ),
            )

        every { crewService.getCrewMembers(any()) } returns members

        // when & then
        mockMvc
            .perform(
                get("/api/v1/crews/{crewId}/members", crewId),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getCrewMembers",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("crewId").description("크루 ID"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("멤버 이름"),
                        fieldWithPath(
                            "data[].profileImage",
                        ).type(JsonFieldType.STRING).description("프로필 이미지 URL").optional(),
                        fieldWithPath(
                            "data[].role",
                        ).type(JsonFieldType.STRING).description("크루 내 역할 (LEADER/MEMBER/GUEST)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun createSchedule() {
        // given
        val memberId = 1L
        val crewId = 1L
        val scheduledAt = LocalDateTime.of(2026, 3, 15, 14, 0)
        val schedule =
            CrewSchedule(
                id = 1L,
                crewId = crewId,
                gymId = 10L,
                title = "주말 볼더링 모임",
                description = "토요일 오후 2시에 만나요!",
                scheduledAt = scheduledAt,
                createdBy = memberId,
            )
        val requestBody =
            """
            {
                "gymId": 10,
                "title": "주말 볼더링 모임",
                "description": "토요일 오후 2시에 만나요!",
                "scheduledAt": "2026-03-15T14:00:00"
            }
            """.trimIndent()

        every { crewService.createSchedule(any(), any(), any(), any(), any(), any()) } returns schedule

        // when & then
        mockMvc
            .perform(
                post("/api/v1/crews/{crewId}/schedules", crewId)
                    .with(authenticatedUser(memberId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "createCrewSchedule",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("crewId").description("크루 ID"),
                    ),
                    requestFields(
                        fieldWithPath("gymId").type(JsonFieldType.NUMBER).description("암장 ID (선택)").optional(),
                        fieldWithPath("title").type(JsonFieldType.STRING).description("일정 제목"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("일정 설명"),
                        fieldWithPath("scheduledAt").type(JsonFieldType.STRING).description("일정 날짜/시간 (ISO 8601)"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 일정 ID"),
                        fieldWithPath("data.crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                        fieldWithPath("data.gymId").type(JsonFieldType.NUMBER).description("암장 ID").optional(),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("일정 제목"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("일정 설명"),
                        fieldWithPath("data.scheduledAt").type(JsonFieldType.STRING).description("일정 날짜/시간"),
                        fieldWithPath("data.createdBy").type(JsonFieldType.NUMBER).description("생성자 멤버 ID"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getSchedules() {
        // given
        val memberId = 1L
        val crewId = 1L
        val schedules =
            listOf(
                CrewSchedule(
                    id = 1L,
                    crewId = crewId,
                    gymId = 10L,
                    title = "주말 볼더링 모임",
                    description = "토요일 오후 2시에 만나요!",
                    scheduledAt = LocalDateTime.of(2026, 3, 15, 14, 0),
                    createdBy = 1L,
                ),
                CrewSchedule(
                    id = 2L,
                    crewId = crewId,
                    gymId = null,
                    title = "정기 회의",
                    description = "월간 크루 회의입니다.",
                    scheduledAt = LocalDateTime.of(2026, 3, 20, 19, 0),
                    createdBy = 2L,
                ),
            )

        every { crewService.getSchedules(any(), any()) } returns schedules

        // when & then
        mockMvc
            .perform(
                get("/api/v1/crews/{crewId}/schedules", crewId)
                    .with(authenticatedUser(memberId)),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getCrewSchedules",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("Bearer {accessToken}"),
                    ),
                    pathParameters(
                        parameterWithName("crewId").description("크루 ID"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("일정 ID"),
                        fieldWithPath("data[].crewId").type(JsonFieldType.NUMBER).description("크루 ID"),
                        fieldWithPath("data[].gymId").type(JsonFieldType.NUMBER).description("암장 ID").optional(),
                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("일정 제목"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("일정 설명"),
                        fieldWithPath("data[].scheduledAt").type(JsonFieldType.STRING).description("일정 날짜/시간"),
                        fieldWithPath("data[].createdBy").type(JsonFieldType.NUMBER).description("생성자 멤버 ID"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getLocalCrews() {
        // given
        val crews =
            listOf(
                Crew(
                    id = 1L,
                    name = "해운대 클라이밍",
                    description = "해운대 클라이밍 크루",
                    maxMemberCount = 50,
                    memberCount = 12,
                    latitude = 35.1796,
                    longitude = 129.0756,
                    address = "부산광역시 해운대구",
                    activityScore = 350.0,
                ),
                Crew(
                    id = 2L,
                    name = "서면 볼더링",
                    description = "서면 볼더링 크루",
                    maxMemberCount = 30,
                    memberCount = 8,
                    latitude = 35.1580,
                    longitude = 129.0596,
                    address = "부산광역시 부산진구 서면",
                    activityScore = 120.0,
                ),
            )

        every { crewService.getLocalCrews(any(), any(), any(), any()) } returns crews

        // when & then
        mockMvc
            .perform(
                get("/api/v1/crews/local")
                    .param("lat", "35.1796")
                    .param("lon", "129.0756")
                    .param("page", "0")
                    .param("size", "20"),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getLocalCrews",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("lat").description("현재 위치 위도"),
                        parameterWithName("lon").description("현재 위치 경도"),
                        parameterWithName("page").description("페이지 번호 (기본 0)").optional(),
                        parameterWithName("size").description("페이지 크기 (기본 20)").optional(),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("크루 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("크루 이름"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("크루 설명"),
                        fieldWithPath("data[].maxMemberCount").type(JsonFieldType.NUMBER).description("최대 인원수"),
                        fieldWithPath("data[].memberCount").type(JsonFieldType.NUMBER).description("현재 멤버 수"),
                        fieldWithPath("data[].address").type(JsonFieldType.STRING).description("크루 주소").optional(),
                        fieldWithPath("data[].activityScore").type(JsonFieldType.NUMBER).description("활동 점수"),
                        fieldWithPath(
                            "data[].activityRank",
                        ).type(JsonFieldType.STRING).description("활동 등급 (노랑단/초록단/빨강단/보라단/황금단)"),
                        fieldWithPath(
                            "data[].activityRankColor",
                        ).type(JsonFieldType.STRING).description("활동 등급 컬러 코드 (HEX)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getCrewRanking() {
        // given
        val crews =
            listOf(
                Crew(
                    id = 3L,
                    name = "전국 1위 크루",
                    description = "활동 점수 최상위 크루",
                    maxMemberCount = 100,
                    memberCount = 45,
                    address = "서울특별시 강남구",
                    activityScore = 1200.0,
                ),
                Crew(
                    id = 1L,
                    name = "해운대 클라이밍",
                    description = "해운대 클라이밍 크루",
                    maxMemberCount = 50,
                    memberCount = 12,
                    address = "부산광역시 해운대구",
                    activityScore = 350.0,
                ),
            )

        every { crewService.getCrewRanking(any(), any()) } returns crews

        // when & then
        mockMvc
            .perform(
                get("/api/v1/crews/ranking")
                    .param("page", "0")
                    .param("size", "20"),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getCrewRanking",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (기본 0)").optional(),
                        parameterWithName("size").description("페이지 크기 (기본 20)").optional(),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("크루 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("크루 이름"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("크루 설명"),
                        fieldWithPath("data[].maxMemberCount").type(JsonFieldType.NUMBER).description("최대 인원수"),
                        fieldWithPath("data[].memberCount").type(JsonFieldType.NUMBER).description("현재 멤버 수"),
                        fieldWithPath("data[].address").type(JsonFieldType.STRING).description("크루 주소").optional(),
                        fieldWithPath("data[].activityScore").type(JsonFieldType.NUMBER).description("활동 점수"),
                        fieldWithPath(
                            "data[].activityRank",
                        ).type(JsonFieldType.STRING).description("활동 등급 (노랑단/초록단/빨강단/보라단/황금단)"),
                        fieldWithPath(
                            "data[].activityRankColor",
                        ).type(JsonFieldType.STRING).description("활동 등급 컬러 코드 (HEX)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }
}
