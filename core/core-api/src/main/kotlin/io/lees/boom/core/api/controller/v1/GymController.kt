package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.GymRadiusSearchRequest
import io.lees.boom.core.api.controller.v1.request.GymSearchRequest
import io.lees.boom.core.api.controller.v1.response.CurrentVisitResponse
import io.lees.boom.core.api.controller.v1.response.GymResponse
import io.lees.boom.core.api.controller.v1.response.GymVisitorResponse
import io.lees.boom.core.api.controller.v1.response.SliceResponse
import io.lees.boom.core.domain.GymService
import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/gyms")
class GymController(
    private val gymService: GymService,
) {
    /**
     * 암장 입장 체크
     */
    @PostMapping("/{gymId}/entry")
    fun enterGym(
        @User memberId: Long, // 인증된 사용자만 호출 가능
        @PathVariable gymId: Long,
    ): ApiResponse<Any> {
        gymService.enterUser(gymId, memberId)
        return ApiResponse.success()
    }

    /**
     * 암장 퇴장 체크
     */
    @PostMapping("/{gymId}/exit")
    fun exitGym(
        @User memberId: Long,
        @PathVariable gymId: Long,
    ): ApiResponse<Any> {
        gymService.exitUser(gymId, memberId)
        return ApiResponse.success()
    }

    /**
     * 방문 연장 (기본 3시간 추가)
     * 만료 10분 전 알림을 받은 사용자가 연장 버튼을 누를 때 호출
     */
    @PostMapping("/{gymId}/extend")
    fun extendVisit(
        @User memberId: Long,
        @PathVariable gymId: Long,
    ): ApiResponse<Any> {
        gymService.extendVisit(gymId, memberId)
        return ApiResponse.success()
    }

    /**
     * 현재 입장중인 암장 조회
     * 입장중인 암장이 없으면 data가 null로 반환됩니다.
     */
    @GetMapping("/my-visit")
    fun getCurrentVisit(
        @User memberId: Long,
    ): ApiResponse<CurrentVisitResponse?> {
        val currentVisit = gymService.getCurrentVisit(memberId)
        return ApiResponse.success(currentVisit?.let { CurrentVisitResponse.of(it) })
    }

    // --- 기존 조회 API (유지) ---

    @GetMapping
    fun getGymsOnMap(
        @User memberId: Long?,
        @ModelAttribute request: GymSearchRequest,
    ): ApiResponse<List<GymResponse>> {
        val gyms =
            gymService.getGymsOnMap(
                southWestLatitude = request.southWestLatitude,
                southWestLongitude = request.southWestLongitude,
                northEastLatitude = request.northEastLatitude,
                northEastLongitude = request.northEastLongitude,
            )

        val responses = gyms.map { GymResponse.of(it) }

        return ApiResponse.success(responses)
    }

    /**
     * 반경 내 암장 조회 (지도용 - 전체 반환)
     * 지도에 마커로 표시할 때 사용
     */
    @GetMapping("/radius")
    fun getGymsByRadius(
        @User memberId: Long?,
        @ModelAttribute request: GymRadiusSearchRequest,
    ): ApiResponse<List<GymResponse>> {
        val gyms =
            gymService.getGymsByRadius(
                latitude = request.latitude,
                longitude = request.longitude,
                radiusKm = request.radiusKm,
            )

        return ApiResponse.success(gyms.map { GymResponse.of(it) })
    }

    /**
     * 반경 내 암장 목록 조회 (목록용 - 무한스크롤 페이징)
     * 리스트 화면에서 사용
     */
    @GetMapping("/radius/list")
    fun getGymsByRadiusList(
        @User memberId: Long?,
        @ModelAttribute request: GymRadiusSearchRequest,
        @ModelAttribute pageRequest: PageRequest,
    ): ApiResponse<SliceResponse<GymResponse>> {
        val sliceResult =
            gymService.getGymsByRadiusSlice(
                latitude = request.latitude,
                longitude = request.longitude,
                radiusKm = request.radiusKm,
                pageRequest = pageRequest,
            )

        return ApiResponse.success(SliceResponse.of(sliceResult) { GymResponse.of(it) })
    }

    /**
     * 암장 입장 유저 목록 조회
     * 특정 암장에 현재 입장 중인 유저 목록을 조회합니다.
     */
    @GetMapping("/{gymId}/visitors")
    fun getGymVisitors(
        @User memberId: Long?,
        @PathVariable gymId: Long,
        @ModelAttribute pageRequest: PageRequest,
    ): ApiResponse<SliceResponse<GymVisitorResponse>> {
        val sliceResult = gymService.getGymVisitors(gymId, pageRequest)

        return ApiResponse.success(SliceResponse.of(sliceResult) { GymVisitorResponse.of(it) })
    }
}
