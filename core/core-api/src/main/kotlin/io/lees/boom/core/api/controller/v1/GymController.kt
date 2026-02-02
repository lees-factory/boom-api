package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.GymRadiusSearchRequest
import io.lees.boom.core.api.controller.v1.request.GymSearchRequest
import io.lees.boom.core.api.controller.v1.response.GymResponse
import io.lees.boom.core.domain.GymService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/gyms")
class GymController(
    private val gymService: GymService,
) {
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

        return ApiResponse.Companion.success(responses)
    }

    @GetMapping("/radius")
    fun getGymsByRadius(
        @User memberId: Long?,
        @ModelAttribute request: GymRadiusSearchRequest,
    ): ApiResponse<List<GymResponse>> {
        // Service에 위임 (도구 레이어를 통해 뷰포트 변환 후 조회됨)
        val gyms =
            gymService.getGymsByRadius(
                latitude = request.latitude,
                longitude = request.longitude,
                radiusKm = request.radiusKm,
            )

        return ApiResponse.success(gyms.map { GymResponse.of(it) })
    }
}
