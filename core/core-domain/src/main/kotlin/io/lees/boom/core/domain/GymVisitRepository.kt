package io.lees.boom.core.domain

interface GymVisitRepository {
    fun save(visit: GymVisit): GymVisit

    // 유저가 어디든 입장해 있는 곳이 있는지?
    fun findActiveVisit(memberId: Long): GymVisit?

    // 특정 암장에 입장해 있는지?
    fun findActiveVisit(
        gymId: Long,
        memberId: Long,
    ): GymVisit?
}
