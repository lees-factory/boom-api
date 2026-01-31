package io.lees.boom.core.domain

interface CrewRepository {
    fun save(crew: Crew): Crew

    fun saveMember(crewMember: CrewMember): CrewMember

    fun findCrewById(crewId: Long): Crew?
    // 필요시 findCrewMemberByCrewIdAndMemberId 등 추가
}
