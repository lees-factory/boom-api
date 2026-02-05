package io.lees.boom.storage.db.core

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface GymActiveVisitJpaRepository : JpaRepository<GymActiveVisitEntity, Long> {
    fun findByMemberId(memberId: Long): GymActiveVisitEntity?

    fun findByGymIdAndMemberId(
        gymId: Long,
        memberId: Long,
    ): GymActiveVisitEntity?

    fun existsByMemberId(memberId: Long): Boolean

    fun deleteByMemberId(memberId: Long)

    fun findByExpiresAtBefore(now: LocalDateTime): List<GymActiveVisitEntity>

    @Query(
        """
        SELECT v.memberId as memberId, m.name as memberName,
               m.profileImage as memberProfileImage, v.admittedAt as admittedAt
        FROM GymActiveVisitEntity v
        JOIN MemberEntity m ON v.memberId = m.id
        WHERE v.gymId = :gymId
        ORDER BY v.admittedAt DESC
        """,
    )
    fun findVisitorsByGymId(
        gymId: Long,
        pageable: Pageable,
    ): List<ActiveVisitorProjection>
}

interface ActiveVisitorProjection {
    val memberId: Long
    val memberName: String
    val memberProfileImage: String?
    val admittedAt: LocalDateTime
}
