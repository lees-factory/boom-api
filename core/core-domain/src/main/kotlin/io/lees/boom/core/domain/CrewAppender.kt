package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewAppender(
    private val crewRepository: CrewRepository,
) {
    suspend fun append(crew: Crew): Crew = crewRepository.save(crew)

    suspend fun appendMember(crewMember: CrewMember): CrewMember = crewRepository.saveMember(crewMember)
}
