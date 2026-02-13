package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.Crew
import io.lees.boom.core.enums.ActivityRank

data class CrewResponse(
    val id: Long,
    val name: String,
    val description: String,
    val crewImage: String?,
    val maxMemberCount: Int,
    val memberCount: Int,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val activityScore: Double,
    val activityRank: String,
    val activityRankColor: String,
) {
    companion object {
        fun of(crew: Crew): CrewResponse {
            val rank = ActivityRank.fromScore(crew.activityScore.toInt())
            return CrewResponse(
                id = crew.id ?: 0L,
                name = crew.name,
                description = crew.description,
                crewImage = crew.crewImage,
                maxMemberCount = crew.maxMemberCount,
                memberCount = crew.memberCount,
                latitude = crew.latitude,
                longitude = crew.longitude,
                address = crew.address,
                activityScore = crew.activityScore,
                activityRank = rank.description,
                activityRankColor = rank.colorHex,
            )
        }
    }
}
