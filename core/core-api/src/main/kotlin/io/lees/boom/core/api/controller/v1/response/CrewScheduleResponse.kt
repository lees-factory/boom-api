package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CrewSchedule
import io.lees.boom.core.domain.CrewScheduleInfo
import java.time.LocalDateTime

data class CrewScheduleResponse(
    val id: Long,
    val crewId: Long,
    val gymId: Long?,
    val title: String,
    val description: String,
    val scheduledAt: LocalDateTime,
    val createdBy: Long,
    val createdByName: String,
) {
    companion object {
        fun from(schedule: CrewScheduleInfo): CrewScheduleResponse =
            CrewScheduleResponse(
                id = schedule.id,
                crewId = schedule.crewId,
                gymId = schedule.gymId,
                title = schedule.title,
                description = schedule.description,
                scheduledAt = schedule.scheduledAt,
                createdBy = schedule.createdBy,
                createdByName = schedule.createdByName,
            )

        fun from(
            schedule: CrewSchedule,
            createdByName: String,
        ): CrewScheduleResponse =
            CrewScheduleResponse(
                id = schedule.id!!,
                crewId = schedule.crewId,
                gymId = schedule.gymId,
                title = schedule.title,
                description = schedule.description,
                scheduledAt = schedule.scheduledAt,
                createdBy = schedule.createdBy,
                createdByName = createdByName,
            )
    }
}
