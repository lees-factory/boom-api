package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.config.UserArgumentResolver
import io.lees.boom.core.domain.Gym
import io.lees.boom.core.domain.GymService
import io.lees.boom.core.domain.Location
import io.lees.boom.test.api.RestDocsTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GymControllerTest : RestDocsTest() {
    private lateinit var gymService: GymService

    @BeforeEach
    fun setUp() {
        gymService = mockk()
        // UserArgumentResolver를 등록해야 @User 어노테이션 및 Header 검증이 동작합니다.
        mockMvc =
            mockController(
                GymController(gymService),
                UserArgumentResolver(),
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
                ),
                Gym.create(
                    name = "락피트",
                    address = "부산광역시 동래구",
                    location = Location.create(35.2387, 129.0914),
                ),
            )

        // Mocking: Service가 호출되면 준비된 암장 리스트를 반환
        every {
            gymService.getGymsOnMap(southWestLat, southWestLon, northEastLat, northEastLon)
        } returns gyms

        // when & then
        mockMvc
            .perform(
                get("/api/v1/gyms")
                    .header("X-User-Id", "1") // 필수 헤더
                    .param("southWestLatitude", southWestLat.toString())
                    .param("southWestLongitude", southWestLon.toString())
                    .param("northEastLatitude", northEastLat.toString())
                    .param("northEastLongitude", northEastLon.toString())
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andDo(
                document(
                    "getGymsOnMap", // 문서 조각(snippet)이 생성될 디렉토리명
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("X-User-Id").description("로그인 사용자 ID"),
                    ),
                    queryParameters(
                        parameterWithName("southWestLatitude").description("남서쪽 (좌측 하단) 위도"),
                        parameterWithName("southWestLongitude").description("남서쪽 (좌측 하단) 경도"),
                        parameterWithName("northEastLatitude").description("북동쪽 (우측 상단) 위도"),
                        parameterWithName("northEastLongitude").description("북동쪽 (우측 상단) 경도"),
                    ),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("응답 결과 (SUCCESS/ERROR)"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("암장 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("암장 이름"),
                        fieldWithPath("data[].address").type(JsonFieldType.STRING).description("암장 주소").optional(),
                        fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                        fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }

    @Test
    fun getGymsByRadius() {
        // given
        val latitude = 35.2326
        val longitude = 129.0642
        val radiusKm = 5.0

        // 반환될 Mock 데이터
        val gyms =
            listOf(
                Gym.create(
                    name = "더클라임",
                    address = "부산광역시 금정구",
                    location = Location.create(35.2326, 129.0642),
                ),
                Gym.create(
                    name = "주변 암장",
                    address = "부산광역시 동래구",
                    location = Location.create(35.2387, 129.0914),
                ),
            )

        // Mocking: Service 호출 시 응답 설정
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
                    "getGymsByRadius", // 문서 식별자
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
                        fieldWithPath("error").type(JsonFieldType.NULL).ignored(),
                    ),
                ),
            )
    }
}
