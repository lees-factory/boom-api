package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewScheduleReader(
    private val crewScheduleRepository: CrewScheduleRepository,
) {
    fun readByCrewId(crewId: Long): List<CrewSchedule> = crewScheduleRepository.findByCrewId(crewId)

    fun readWithCreatorByCrewId(crewId: Long): List<CrewScheduleInfo> = crewScheduleRepository.findWithCreatorByCrewId(crewId)

    fun readById(scheduleId: Long): CrewSchedule? = crewScheduleRepository.findById(scheduleId)

    fun readParticipant(
        scheduleId: Long,
        memberId: Long,
    ): CrewScheduleParticipant? = crewScheduleRepository.findParticipant(scheduleId, memberId)

    fun readParticipants(scheduleId: Long): List<CrewScheduleParticipantInfo> =
        crewScheduleRepository.findParticipantsByScheduleId(scheduleId)

    fun readParticipantMemberIds(scheduleId: Long): List<Long> =
        crewScheduleRepository.findParticipantMemberIdsByScheduleId(scheduleId)
}
