package io.lees.boom.core.domain.crew

interface CrewRepository {
    fun save(crew: Crew): Crew

    fun saveMember(crewMember: CrewMember): CrewMember

    /**
     * 크루 생성 + 리더 등록을 원자적으로 처리
     */
    fun saveCrewWithLeader(
        crew: Crew,
        leaderId: Long,
    ): Crew

    fun updateCrew(
        crewId: Long,
        maxMemberCount: Int?,
        crewImage: String?,
    )

    fun findCrewById(crewId: Long): Crew?

    fun findCrewByIdForUpdate(crewId: Long): Crew?

    fun findCrewIdsByMemberId(memberId: Long): List<Long>

    fun findMemberIdsByCrewIds(crewIds: List<Long>): Set<Long>

    fun findMyCrews(memberId: Long): List<MyCrewInfo>

    fun findMembersWithInfoByCrewId(crewId: Long): List<CrewMemberInfo>

    fun findMemberByCrewIdAndMemberId(
        crewId: Long,
        memberId: Long,
    ): CrewMember?

    fun softDeleteCrew(crewId: Long)

    fun softDeleteMember(crewMemberId: Long)

    fun softDeleteAllMembersByCrewId(crewId: Long)

    fun countLeadersByCrewId(crewId: Long): Long

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

    fun findCrewRankingByAvgScore(
        page: Int,
        size: Int,
    ): List<CrewRankingInfo>
}
