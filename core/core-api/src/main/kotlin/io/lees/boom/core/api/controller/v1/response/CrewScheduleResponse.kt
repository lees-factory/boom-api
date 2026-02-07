package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CrewSchedule
import java.time.LocalDateTime

data class CrewScheduleResponse(
    val id: Long,
    val crewId: Long,
    val gymId: Long?,
    val title: String,
    val description: String,
    val scheduledAt: LocalDateTime,
    val createdBy: Long,
) {
    companion object {
        fun from(schedule: CrewSchedule): CrewScheduleResponse =
            CrewScheduleResponse(
                id = schedule.id!!,
                crewId = schedule.crewId,
                gymId = schedule.gymId,
                title = schedule.title,
                description = schedule.description,
                scheduledAt = schedule.scheduledAt,
                createdBy = schedule.createdBy,
            )
    }
}
