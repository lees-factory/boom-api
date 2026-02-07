package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewReader(
    private val crewRepository: CrewRepository,
) {
    fun readMyCrews(memberId: Long): List<MyCrewInfo> = crewRepository.findMyCrews(memberId)

    fun readById(crewId: Long): Crew? = crewRepository.findCrewById(crewId)
}
