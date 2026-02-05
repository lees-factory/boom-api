package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class GymActiveVisitWriter(
    private val gymActiveVisitRepository: GymActiveVisitRepository,
) {
    /**
     * 입장 처리 (UPSERT)
     */
    fun save(activeVisit: GymActiveVisit): GymActiveVisit = gymActiveVisitRepository.save(activeVisit)

    /**
     * 퇴장 처리 (DELETE)
     */
    fun delete(memberId: Long) {
        gymActiveVisitRepository.delete(memberId)
    }
}
