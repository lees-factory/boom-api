package io.lees.boom.core.domain

import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import java.time.LocalDateTime

interface GymVisitRepository {
    fun save(visit: GymVisit): GymVisit

    fun findById(id: Long): GymVisit?

    // 유저가 어디든 입장해 있는 곳이 있는지?
    fun findActiveVisit(memberId: Long): GymVisit?

    // 특정 암장에 입장해 있는지?
    fun findActiveVisit(
        gymId: Long,
        memberId: Long,
    ): GymVisit?

    // 오래된 방문 목록 조회 (입장 시간이 threshold 이전인 방문)
    fun findStaleVisits(threshold: LocalDateTime): List<GymVisit>

    // 특정 암장의 현재 입장 유저 목록 조회 (페이징)
    fun findActiveVisitorsByGymId(
        gymId: Long,
        pageRequest: PageRequest,
    ): SliceResult<GymVisitor>
}
