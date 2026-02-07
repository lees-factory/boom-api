package io.lees.boom.core.domain

import io.lees.boom.core.enums.CrewRole

data class MyCrewInfo(
    val crewId: Long,
    val name: String,
    val description: String,
    val maxMemberCount: Int,
    val myRole: CrewRole,
    val memberCount: Long,
)
