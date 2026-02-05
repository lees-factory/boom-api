package io.lees.boom.storage.db.core

import io.lees.boom.core.enums.VisitStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface GymVisitJpaRepository : JpaRepository<GymVisitEntity, Long> {
    // 특정 유저가 '입장 중'인 기록이 있는지 확인
    fun findByMemberIdAndStatus(
        memberId: Long,
        status: VisitStatus,
    ): GymVisitEntity?

    // 특정 유저가 특정 암장에 '입장 중'인지 확인
    fun findByGymIdAndMemberIdAndStatus(
        gymId: Long,
        memberId: Long,
        status: VisitStatus,
    ): GymVisitEntity?

    // 오래된 방문 목록 조회 (입장 중 & 입장시간이 threshold 이전)
    fun findByStatusAndAdmittedAtBefore(
        status: VisitStatus,
        threshold: LocalDateTime,
    ): List<GymVisitEntity>
}
