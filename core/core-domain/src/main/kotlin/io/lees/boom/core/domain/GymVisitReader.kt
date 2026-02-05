package io.lees.boom.core.domain

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
}
