package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class GymVisitAppender(
    private val gymVisitRepository: GymVisitRepository,
) {
    fun append(visit: GymVisit): GymVisit = gymVisitRepository.save(visit)
}
