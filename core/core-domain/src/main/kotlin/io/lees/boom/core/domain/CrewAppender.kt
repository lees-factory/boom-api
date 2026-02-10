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

    fun updateCrew(
        crewId: Long,
        maxMemberCount: Int?,
        crewImage: String?,
    ) = crewRepository.updateCrew(crewId, maxMemberCount, crewImage)

    fun softDeleteCrew(crewId: Long) = crewRepository.softDeleteCrew(crewId)

    fun softDeleteMember(crewMemberId: Long) = crewRepository.softDeleteMember(crewMemberId)

    fun softDeleteAllMembersByCrewId(crewId: Long) = crewRepository.softDeleteAllMembersByCrewId(crewId)
}
