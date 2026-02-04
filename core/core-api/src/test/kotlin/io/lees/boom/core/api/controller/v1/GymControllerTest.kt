package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.domain.Gym
import io.lees.boom.core.domain.GymService
import io.lees.boom.core.domain.Location
import io.lees.boom.core.enums.CrowdLevel
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GymControllerTest : RestDocsTest() {
    private lateinit var gymService: GymService

    @BeforeEach
    fun setUp() {
        gymService = mockk()
        mockMvc =
            mockController(
                GymController(gymService),
                UserArgumentResolver(),
            )
    }

    @Test
    fun enterGym() {
        // given
        val gymId = 1L
        val memberId = 1L
        justRun { gymService.enterUser(gymId, memberId) }

        // when & then
        mockMvc
            .perform(
                post("/api/v1/gyms/{gymId}/entry", gymId)
                    .header("X-User-Id", memberId.toString())
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "enterGym",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-Id").description("로그인 사용자 ID"),
                    ),
                    pathParameters(
                        parameterWithName("gymId").description("입장할 암장 ID"),
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
    fun exitGym() {
        // given
        val gymId = 1L
        val memberId = 1L
        justRun { gymService.exitUser(gymId, memberId) }

        // when & then
        mockMvc
            .perform(
                post("/api/v1/gyms/{gymId}/exit", gymId)
                    .header("X-User-Id", memberId.toString())
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "exitGym",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-Id").description("로그인 사용자 ID"),
                    ),
                    pathParameters(
                        parameterWithName("gymId").description("퇴장할 암장 ID"),
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
    fun getGymsOnMap() {
        // given
        val southWestLat = 35.22
        val southWestLon = 129.06
        val northEastLat = 35.24
        val northEastLon = 129.10

        val gyms =
            listOf(
                Gym.create(
                    name = "더클라임",
                    address = "부산광역시 금정구",
                    location = Location.create(35.2326, 129.0642),
                    maxCapacity = 50,
                    currentCount = 45,
                    crowdLevel = CrowdLevel.CROWDED,
                ),
            )

        every {
            gymService.getGymsOnMap(southWestLat, southWestLon, northEastLat, northEastLon)
        } returns gyms

        // when & then
        mockMvc
            .perform(
                get("/api/v1/gyms")
                    .header("X-User-Id", "1")
                    .param("southWestLatitude", southWestLat.toString())
                    .param("southWestLongitude", southWestLon.toString())
                    .param("northEastLatitude", northEastLat.toString())
                    .param("northEastLongitude", northEastLon.toString())
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getGymsOnMap",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-Id").description("로그인 사용자 ID"),
                    ),
                    queryParameters(
                        parameterWithName("southWestLatitude").description("남서쪽 위도"),
                        parameterWithName("southWestLongitude").description("남서쪽 경도"),
                        parameterWithName("northEastLatitude").description("북동쪽 위도"),
                        parameterWithName("northEastLongitude").description("북동쪽 경도"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("암장 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("암장 이름"),
                        fieldWithPath("data[].address").type(JsonFieldType.STRING).description("암장 주소").optional(),
                        fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                        fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                        // [추가된 필드 검증]
                        fieldWithPath("data[].maxCapacity").type(JsonFieldType.NUMBER).description("최대 수용 인원"),
                        fieldWithPath("data[].currentCount").type(JsonFieldType.NUMBER).description("현재 인원"),
                        fieldWithPath(
                            "data[].crowdLevel",
                        ).type(JsonFieldType.STRING).description("혼잡도 상태 (RELAXED, NORMAL, CROWDED)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getGymsByRadius() {
        // given
        val latitude = 35.23
        val longitude = 129.07
        val radiusKm = 5.0

        val gyms =
            listOf(
                Gym.create(
                    name = "더클라임",
                    address = "부산광역시 금정구",
                    location = Location.create(35.2326, 129.0642),
                    maxCapacity = 50,
                    currentCount = 45,
                    crowdLevel = CrowdLevel.CROWDED,
                ),
            )

        every {
            gymService.getGymsByRadius(latitude, longitude, radiusKm)
        } returns gyms

        // when & then
        mockMvc
            .perform(
                get("/api/v1/gyms/radius")
                    .header("X-User-Id", "1")
                    .param("latitude", latitude.toString())
                    .param("longitude", longitude.toString())
                    .param("radiusKm", radiusKm.toString())
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getGymsByRadius",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-Id").description("로그인 사용자 ID"),
                    ),
                    queryParameters(
                        parameterWithName("latitude").description("중심점 위도"),
                        parameterWithName("longitude").description("중심점 경도"),
                        parameterWithName("radiusKm").description("검색 반경 (km)"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("암장 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("암장 이름"),
                        fieldWithPath("data[].address").type(JsonFieldType.STRING).description("암장 주소").optional(),
                        fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                        fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                        fieldWithPath("data[].maxCapacity").type(JsonFieldType.NUMBER).description("최대 수용 인원"),
                        fieldWithPath("data[].currentCount").type(JsonFieldType.NUMBER).description("현재 인원"),
                        fieldWithPath(
                            "data[].crowdLevel",
                        ).type(JsonFieldType.STRING).description("혼잡도 상태 (RELAXED, NORMAL, CROWDED)"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }
}
