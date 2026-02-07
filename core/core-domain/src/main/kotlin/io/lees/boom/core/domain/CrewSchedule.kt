package io.lees.boom.core.domain

import java.time.LocalDateTime

data class CrewSchedule(
    val id: Long? = null,
    val crewId: Long,
    val gymId: Long?,
    val title: String,
    val description: String,
    val scheduledAt: LocalDateTime,
    val createdBy: Long,
) {
    companion object {
        fun create(
            crewId: Long,
            gymId: Long?,
            title: String,
            description: String,
            scheduledAt: LocalDateTime,
            createdBy: Long,
        ): CrewSchedule =
            CrewSchedule(
                crewId = crewId,
                gymId = gymId,
                title = title,
                description = description,
                scheduledAt = scheduledAt,
                createdBy = createdBy,
            )
    }
}
