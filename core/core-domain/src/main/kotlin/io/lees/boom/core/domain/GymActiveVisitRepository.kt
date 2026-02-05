package io.lees.boom.core.domain

import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import java.time.LocalDateTime

interface GymActiveVisitRepository {
    /**
     * 저장 또는 업데이트 (UPSERT)
     */
    fun save(activeVisit: GymActiveVisit): GymActiveVisit

    /**
     * 삭제 (퇴장 시)
     */
    fun delete(memberId: Long)

    /**
     * 특정 유저의 현재 입장 조회
     */
    fun findByMemberId(memberId: Long): GymActiveVisit?

    /**
     * 특정 암장에 특정 유저의 현재 입장 조회
     */
    fun findByGymIdAndMemberId(
        gymId: Long,
        memberId: Long,
    ): GymActiveVisit?

    /**
     * 특정 유저가 현재 입장중인지 확인
     */
    fun existsByMemberId(memberId: Long): Boolean

    /**
     * 만료된 입장 목록 조회 (스케줄러용)
     */
    fun findExpiredVisits(now: LocalDateTime): List<GymActiveVisit>

    /**
     * 특정 암장의 현재 입장 유저 목록 조회 (페이징)
     */
    fun findVisitorsByGymId(
        gymId: Long,
        pageRequest: PageRequest,
    ): SliceResult<GymVisitor>
}
