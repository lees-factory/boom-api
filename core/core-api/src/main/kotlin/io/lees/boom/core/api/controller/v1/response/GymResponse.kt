package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.Gym

data class GymResponse(
    val id: Long,
    val name: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        // 도메인 -> 응답 DTO 변환 (Mapper)
        fun of(gym: Gym): GymResponse =
            GymResponse(
                id = gym.id ?: 0L,
                name = gym.name,
                address = gym.address,
                latitude = gym.location.latitude,
                longitude = gym.location.longitude,
            )
    }
}
