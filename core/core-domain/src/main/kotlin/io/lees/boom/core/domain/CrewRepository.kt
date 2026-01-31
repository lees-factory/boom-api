package io.lees.boom.core.domain

interface CrewRepository {
    suspend fun save(crew: Crew): Crew

    suspend fun saveMember(crewMember: CrewMember): CrewMember

    suspend fun findCrewById(crewId: Long): Crew?
    // 필요시 findCrewMemberByCrewIdAndMemberId 등 추가
}
