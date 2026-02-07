package io.lees.boom.core.domain

interface CrewScheduleRepository {
    fun save(schedule: CrewSchedule): CrewSchedule

    fun findByCrewId(crewId: Long): List<CrewSchedule>

    fun findById(scheduleId: Long): CrewSchedule?

    fun saveParticipant(participant: CrewScheduleParticipant): CrewScheduleParticipant

    fun findParticipant(
        scheduleId: Long,
        memberId: Long,
    ): CrewScheduleParticipant?

    fun findParticipantsByScheduleId(scheduleId: Long): List<CrewScheduleParticipantInfo>
}
