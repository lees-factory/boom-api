package io.lees.boom.storage.db.core

import io.lees.boom.core.enums.VisitStatus
import org.springframework.data.jpa.repository.JpaRepository

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
}
