package io.lees.boom.core.domain.crew

import java.time.LocalDateTime

data class CrewScheduleParticipantInfo(
    val memberId: Long,
    val name: String,
    val profileImage: String?,
    val participatedAt: LocalDateTime,
    val isCreator: Boolean = false,
)
