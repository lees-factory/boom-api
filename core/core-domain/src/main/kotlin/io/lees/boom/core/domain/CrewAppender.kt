package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewAppender(
    private val crewRepository: CrewRepository,
) {
    fun append(crew: Crew): Crew = crewRepository.save(crew)

    fun appendMember(crewMember: CrewMember): CrewMember = crewRepository.saveMember(crewMember)

    fun appendCrewWithLeader(
        crew: Crew,
        leaderId: Long,
    ): Crew = crewRepository.saveCrewWithLeader(crew, leaderId)

    fun appendMemberWithCount(crewMember: CrewMember): CrewMember = crewRepository.addMemberWithCount(crewMember)

    fun softDeleteCrew(crewId: Long) = crewRepository.softDeleteCrew(crewId)

    fun softDeleteMember(crewMemberId: Long) = crewRepository.softDeleteMember(crewMemberId)

    fun softDeleteAllMembersByCrewId(crewId: Long) = crewRepository.softDeleteAllMembersByCrewId(crewId)

    fun decrementMemberCount(crewId: Long) = crewRepository.decrementMemberCount(crewId)
}
