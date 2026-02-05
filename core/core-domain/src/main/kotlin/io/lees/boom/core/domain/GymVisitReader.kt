package io.lees.boom.core.domain

import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GymVisitReader(
    private val gymVisitRepository: GymVisitRepository,
) {
    companion object {
        private const val STALE_HOURS = 24L
    }

    fun existsActiveVisit(memberId: Long): Boolean = gymVisitRepository.findActiveVisit(memberId) != null

    fun readActiveVisit(memberId: Long): GymVisit? = gymVisitRepository.findActiveVisit(memberId)

    fun readActiveVisit(
        gymId: Long,
        memberId: Long,
    ): GymVisit? = gymVisitRepository.findActiveVisit(gymId, memberId)

    /**
     * 24시간 이상 지난 입장 상태의 방문 조회
     * 앱이 죽거나 삭제된 경우 등 예외 상황 정리용
     */
    fun readStaleVisits(now: LocalDateTime = LocalDateTime.now()): List<GymVisit> =
        gymVisitRepository.findStaleVisits(now.minusHours(STALE_HOURS))

    /**
     * 특정 암장의 현재 입장 유저 목록 조회 (페이징)
     */
    fun readActiveVisitors(
        gymId: Long,
        pageRequest: PageRequest,
    ): SliceResult<GymVisitor> = gymVisitRepository.findActiveVisitorsByGymId(gymId, pageRequest)
}
