package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class GymVisitReader(
    private val gymVisitRepository: GymVisitRepository,
) {
    fun existsActiveVisit(memberId: Long): Boolean = gymVisitRepository.findActiveVisit(memberId) != null

    fun readActiveVisit(
        gymId: Long,
        memberId: Long,
    ): GymVisit? = gymVisitRepository.findActiveVisit(gymId, memberId)
}
