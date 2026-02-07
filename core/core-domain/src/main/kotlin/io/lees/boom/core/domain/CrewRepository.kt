package io.lees.boom.core.domain

interface CrewRepository {
    fun save(crew: Crew): Crew

    fun saveMember(crewMember: CrewMember): CrewMember

    /**
     * 크루 생성 + 리더 등록 + 멤버 수 증가를 원자적으로 처리
     */
    fun saveCrewWithLeader(
        crew: Crew,
        leaderId: Long,
    ): Crew

    /**
     * 크루 멤버 저장 + 멤버 수 증가를 원자적으로 처리
     */
    fun addMemberWithCount(crewMember: CrewMember): CrewMember

    fun findCrewById(crewId: Long): Crew?

    fun findCrewIdsByMemberId(memberId: Long): List<Long>

    fun findMemberIdsByCrewIds(crewIds: List<Long>): Set<Long>

    fun findMyCrews(memberId: Long): List<MyCrewInfo>

    fun findMembersWithInfoByCrewId(crewId: Long): List<CrewMemberInfo>

    fun findMemberByCrewIdAndMemberId(
        crewId: Long,
        memberId: Long,
    ): CrewMember?

    fun incrementMemberCount(crewId: Long)

    fun findCrewsByLocation(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): List<Crew>

    fun findCrewRanking(
        page: Int,
        size: Int,
    ): List<Crew>
}
