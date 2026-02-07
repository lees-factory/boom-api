package io.lees.boom.core.domain

import java.time.LocalDateTime

data class CrewScheduleParticipantInfo(
    val memberId: Long,
    val name: String,
    val profileImage: String?,
    val participatedAt: LocalDateTime,
)
