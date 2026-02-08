package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewScheduleAppender(
    private val crewScheduleRepository: CrewScheduleRepository,
) {
    fun append(schedule: CrewSchedule): CrewSchedule = crewScheduleRepository.save(schedule)

    fun appendParticipant(participant: CrewScheduleParticipant): CrewScheduleParticipant =
        crewScheduleRepository.saveParticipant(participant)

    fun removeParticipant(
        scheduleId: Long,
        memberId: Long,
    ) = crewScheduleRepository.deleteParticipant(scheduleId, memberId)

    fun removeSchedule(scheduleId: Long) = crewScheduleRepository.deleteSchedule(scheduleId)

    fun removeParticipantsByScheduleId(scheduleId: Long) =
        crewScheduleRepository.deleteParticipantsByScheduleId(scheduleId)
}
