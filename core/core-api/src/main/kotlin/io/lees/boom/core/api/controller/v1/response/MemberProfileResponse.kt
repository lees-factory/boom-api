package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.BadgeResult
import io.lees.boom.core.domain.Member
import io.lees.boom.core.enums.ActivityRank

data class MemberProfileResponse(
    val id: Long,
    val name: String,
    val profileImage: String?,
    val activityScore: Int,
    val activityRank: String,
    val activityRankColor: String,
    val badges: List<BadgeResponse>,
) {
    companion object {
        fun of(
            member: Member,
            badgeResults: List<BadgeResult>,
        ): MemberProfileResponse {
            val rank = ActivityRank.fromScore(member.activityScore)
            return MemberProfileResponse(
                id = member.id!!,
                name = member.name,
                profileImage = member.profileImage,
                activityScore = member.activityScore,
                activityRank = rank.description,
                activityRankColor = rank.colorHex,
                badges = badgeResults.map { BadgeResponse.from(it) },
            )
        }
    }
}
