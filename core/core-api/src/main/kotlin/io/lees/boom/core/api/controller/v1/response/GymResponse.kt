package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.crew.GymCrewMemberInfo
import io.lees.boom.core.domain.gym.Gym
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
    val crewMemberCount: Int = 0,
    val crewMembers: List<CrewMemberVisitResponse> = emptyList(),
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

        fun of(
            gym: Gym,
            crewMembers: List<GymCrewMemberInfo>,
        ): GymResponse =
            GymResponse(
                id = gym.id ?: 0L,
                name = gym.name,
                address = gym.address,
                latitude = gym.location.latitude,
                longitude = gym.location.longitude,
                maxCapacity = gym.maxCapacity,
                currentCount = gym.currentCount,
                crowdLevel = gym.crowdLevel,
                crewMemberCount = crewMembers.size,
                crewMembers = crewMembers.map { CrewMemberVisitResponse.of(it) },
            )
    }
}
