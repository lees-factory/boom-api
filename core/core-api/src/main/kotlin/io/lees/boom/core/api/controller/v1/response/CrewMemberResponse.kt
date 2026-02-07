package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CrewMemberInfo
import io.lees.boom.core.enums.CrewRole

data class CrewMemberResponse(
    val memberId: Long,
    val name: String,
    val profileImage: String?,
    val role: CrewRole,
) {
    companion object {
        fun from(info: CrewMemberInfo): CrewMemberResponse =
            CrewMemberResponse(
                memberId = info.memberId,
                name = info.name,
                profileImage = info.profileImage,
                role = info.role,
            )
    }
}
