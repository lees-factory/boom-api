package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.crew.GymCrewMemberInfo

data class CrewMemberVisitResponse(
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
) {
    companion object {
        fun of(info: GymCrewMemberInfo): CrewMemberVisitResponse =
            CrewMemberVisitResponse(
                memberId = info.memberId,
                memberName = info.memberName,
                memberProfileImage = info.memberProfileImage,
            )
    }
}
