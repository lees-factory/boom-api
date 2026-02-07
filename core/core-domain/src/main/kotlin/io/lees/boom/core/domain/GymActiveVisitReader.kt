package io.lees.boom.core.domain

import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GymActiveVisitReader(
    private val gymActiveVisitRepository: GymActiveVisitRepository,
) {
    /**
     * 특정 유저가 현재 입장중인지 확인
     */
    fun existsActiveVisit(memberId: Long): Boolean = gymActiveVisitRepository.existsByMemberId(memberId)

    /**
     * 특정 유저의 현재 입장 조회
     */
    fun readActiveVisit(memberId: Long): GymActiveVisit? = gymActiveVisitRepository.findByMemberId(memberId)

    /**
     * 특정 암장에 특정 유저의 현재 입장 조회
     */
    fun readActiveVisit(
        gymId: Long,
        memberId: Long,
    ): GymActiveVisit? = gymActiveVisitRepository.findByGymIdAndMemberId(gymId, memberId)

    /**
     * 만료된 입장 목록 조회 (스케줄러용)
     */
    fun readExpiredVisits(now: LocalDateTime = LocalDateTime.now()): List<GymActiveVisit> =
        gymActiveVisitRepository.findExpiredVisits(now)

    /**
     * 특정 암장의 현재 입장 유저 목록 조회 (페이징)
     */
    fun readActiveVisitors(
        gymId: Long,
        pageRequest: PageRequest,
    ): SliceResult<GymVisitor> = gymActiveVisitRepository.findVisitorsByGymId(gymId, pageRequest)

    /**
     * 크루원들의 암장별 입장 정보 배치 조회
     */
    fun readCrewMemberVisits(
        memberIds: Set<Long>,
        gymIds: Set<Long>,
    ): Map<Long, List<GymCrewMemberInfo>> = gymActiveVisitRepository.findCrewMemberVisits(memberIds, gymIds)
}
