package io.lees.boom.core.domain.gym

import org.springframework.stereotype.Component

@Component
class GymVisitAppender(
    private val gymVisitRepository: GymVisitRepository,
) {
    fun append(visit: GymVisit): GymVisit = gymVisitRepository.save(visit)

    fun deleteAllByMemberId(memberId: Long) = gymVisitRepository.deleteAllByMemberId(memberId)
}
