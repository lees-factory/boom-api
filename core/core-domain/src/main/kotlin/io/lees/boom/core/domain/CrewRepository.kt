package io.lees.boom.core.domain

interface CrewRepository {
    fun save(crew: Crew): Crew

    fun saveMember(crewMember: CrewMember): CrewMember

    fun findCrewById(crewId: Long): Crew?

    fun findCrewIdsByMemberId(memberId: Long): List<Long>

    fun findMemberIdsByCrewIds(crewIds: List<Long>): Set<Long>

    fun findMyCrews(memberId: Long): List<MyCrewInfo>

    fun findMembersWithInfoByCrewId(crewId: Long): List<CrewMemberInfo>

    fun findMemberByCrewIdAndMemberId(
        crewId: Long,
        memberId: Long,
    ): CrewMember?
}
