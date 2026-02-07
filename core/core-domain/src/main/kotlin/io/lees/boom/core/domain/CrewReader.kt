package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class CrewReader(
    private val crewRepository: CrewRepository,
) {
    fun readMyCrews(memberId: Long): List<MyCrewInfo> = crewRepository.findMyCrews(memberId)

    fun readById(crewId: Long): Crew? = crewRepository.findCrewById(crewId)

    // [추가] 동네 크루 찾기
    fun readLocalCrews(
        latitude: Double,
        longitude: Double,
        page: Int,
        size: Int,
    ): List<Crew> {
        // 반경 5km 내 검색 (정책에 따라 변경 가능)
        val searchRadiusKm = 5.0
        return crewRepository.findCrewsByLocation(latitude, longitude, searchRadiusKm, page, size)
    }

    // [추가] 랭킹 순 조회
    fun readCrewRanking(
        page: Int,
        size: Int,
    ): List<Crew> = crewRepository.findCrewRanking(page, size)

    // 크루원 평균 활동 점수 기반 랭킹
    fun readCrewRankingByAvgScore(
        page: Int,
        size: Int,
    ): List<CrewRankingInfo> = crewRepository.findCrewRankingByAvgScore(page, size)
}
