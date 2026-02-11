package io.lees.boom.core.domain

import java.time.LocalDateTime

data class CrewScheduleInfo(
    val id: Long,
    val crewId: Long,
    val gymId: Long?,
    val title: String,
    val description: String,
    val scheduledAt: LocalDateTime,
    val createdBy: Long,
    val createdByName: String,
)