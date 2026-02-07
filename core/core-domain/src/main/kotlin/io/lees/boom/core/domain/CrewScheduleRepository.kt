package io.lees.boom.core.domain

interface CrewScheduleRepository {
    fun save(schedule: CrewSchedule): CrewSchedule

    fun findByCrewId(crewId: Long): List<CrewSchedule>
}
