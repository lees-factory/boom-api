package io.lees.boom.core.domain.crew

import io.lees.boom.core.enums.CrewRole

data class CrewMemberInfo(
    val memberId: Long,
    val name: String,
    val profileImage: String?,
    val role: CrewRole,
)
