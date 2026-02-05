package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.GymRadiusSearchRequest
import io.lees.boom.core.api.controller.v1.request.GymSearchRequest
import io.lees.boom.core.api.controller.v1.response.GymResponse
import io.lees.boom.core.domain.GymService
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
}
