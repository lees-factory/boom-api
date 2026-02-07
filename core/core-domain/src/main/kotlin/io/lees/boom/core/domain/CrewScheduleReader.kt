package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewScheduleReader(
    private val crewScheduleRepository: CrewScheduleRepository,
) {
    fun readByCrewId(crewId: Long): List<CrewSchedule> = crewScheduleRepository.findByCrewId(crewId)
}
