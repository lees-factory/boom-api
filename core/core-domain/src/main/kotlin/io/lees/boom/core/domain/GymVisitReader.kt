package io.lees.boom.core.domain

import org.springframework.stereotype.Component

/**
 * 방문 히스토리 조회 (통계용)
 */
@Component
class GymVisitReader(
    private val gymVisitRepository: GymVisitRepository,
) {
    fun findById(id: Long): GymVisit? = gymVisitRepository.findById(id)
}
