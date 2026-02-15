package io.lees.boom.core.domain.crew

interface CrewScheduleRepository {
    fun save(schedule: CrewSchedule): CrewSchedule

    fun findByCrewId(crewId: Long): List<CrewSchedule>

    fun findWithCreatorByCrewId(crewId: Long): List<CrewScheduleInfo>

    fun findById(scheduleId: Long): CrewSchedule?

    fun saveParticipant(participant: CrewScheduleParticipant): CrewScheduleParticipant

    fun findParticipant(
        scheduleId: Long,
        memberId: Long,
    ): CrewScheduleParticipant?

    fun findParticipantsByScheduleId(scheduleId: Long): List<CrewScheduleParticipantInfo>

    fun deleteParticipant(
        scheduleId: Long,
        memberId: Long,
    )

    fun deleteSchedule(scheduleId: Long)

    fun deleteParticipantsByScheduleId(scheduleId: Long)

    fun findParticipantMemberIdsByScheduleId(scheduleId: Long): List<Long>
}
