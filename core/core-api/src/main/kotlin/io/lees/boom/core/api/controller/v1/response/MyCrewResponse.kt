package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.MyCrewInfo
import io.lees.boom.core.enums.CrewRole

data class MyCrewResponse(
    val crewId: Long,
    val name: String,
    val description: String,
    val crewImage: String?,
    val maxMemberCount: Int,
    val myRole: CrewRole,
    val memberCount: Long,
) {
    companion object {
        fun from(info: MyCrewInfo): MyCrewResponse =
            MyCrewResponse(
                crewId = info.crewId,
                name = info.name,
                description = info.description,
                crewImage = info.crewImage,
                maxMemberCount = info.maxMemberCount,
                myRole = info.myRole,
                memberCount = info.memberCount,
            )
    }
}
