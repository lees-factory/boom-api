package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CrewRankingInfo

data class CrewRankingResponse(
    val crewId: Long,
    val name: String,
    val description: String,
    val memberCount: Int,
    val maxMemberCount: Int,
    val avgScore: Double,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
) {
    companion object {
        fun from(info: CrewRankingInfo): CrewRankingResponse =
            CrewRankingResponse(
                crewId = info.crewId,
                name = info.name,
                description = info.description,
                memberCount = info.memberCount,
                maxMemberCount = info.maxMemberCount,
                avgScore = info.avgScore,
                latitude = info.latitude,
                longitude = info.longitude,
                address = info.address,
            )
    }
}
