package io.lees.boom.core.api.controller.v1.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.lees.boom.core.domain.Member
import io.lees.boom.core.enums.ActivityRank

data class MemberProfileResponse(
    val id: Long,
    val name: String,
    val profileImage: String?,
    val activityScore: Int,
    val activityRank: String,
    val activityRankColor: String,
    @get:JsonProperty("isBlocked")
    val isBlocked: Boolean,
) {
    companion object {
        fun of(
            member: Member,
            isBlocked: Boolean = false,
        ): MemberProfileResponse {
            val rank = ActivityRank.fromScore(member.activityScore)
            return MemberProfileResponse(
                id = member.id!!,
                name = member.name,
                profileImage = member.profileImage,
                activityScore = member.activityScore,
                activityRank = rank.description,
                activityRankColor = rank.colorHex,
                isBlocked = isBlocked,
            )
        }
    }
}
