package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.Gym
import io.lees.boom.core.enums.CrowdLevel

data class GymResponse(
    val id: Long,
    val name: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    // [추가] 클라이언트 렌더링용 필드
    val maxCapacity: Int,
    val currentCount: Int,
    val crowdLevel: CrowdLevel,
) {
    companion object {
        fun of(gym: Gym): GymResponse =
            GymResponse(
                id = gym.id ?: 0L,
                name = gym.name,
                address = gym.address,
                latitude = gym.location.latitude,
                longitude = gym.location.longitude,
                maxCapacity = gym.maxCapacity,
                currentCount = gym.currentCount,
                crowdLevel = gym.crowdLevel,
            )
    }
}
