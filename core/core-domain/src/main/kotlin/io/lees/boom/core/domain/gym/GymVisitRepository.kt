package io.lees.boom.core.domain.gym

/**
 * 방문 히스토리 저장소 (통계용)
 * - 입장/퇴장 시 항상 INSERT
 * - 모든 방문 기록 보존
 */
interface GymVisitRepository {
    /**
     * 방문 기록 저장 (항상 INSERT)
     */
    fun save(visit: GymVisit): GymVisit

    fun findById(id: Long): GymVisit?
}
