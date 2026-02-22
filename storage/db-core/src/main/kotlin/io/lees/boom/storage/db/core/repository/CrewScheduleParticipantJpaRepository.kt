package io.lees.boom.storage.db.core.repository

import io.lees.boom.storage.db.core.entity.CrewScheduleParticipantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface CrewScheduleParticipantJpaRepository : JpaRepository<CrewScheduleParticipantEntity, Long> {
    fun findByScheduleIdAndMemberId(
        scheduleId: Long,
        memberId: Long,
    ): CrewScheduleParticipantEntity?

    fun deleteByScheduleIdAndMemberId(
        scheduleId: Long,
        memberId: Long,
    )

    fun deleteAllByScheduleId(scheduleId: Long)

    fun deleteAllByMemberId(memberId: Long)

    fun findAllByScheduleId(scheduleId: Long): List<CrewScheduleParticipantEntity>

    @Query(
        """
        SELECT p.memberId as memberId, m.name as name,
               m.profileImage as profileImage, p.participatedAt as participatedAt
        FROM CrewScheduleParticipantEntity p
        JOIN MemberEntity m ON p.memberId = m.id
        WHERE p.scheduleId = :scheduleId
        ORDER BY p.participatedAt ASC
        """,
    )
    fun findParticipantsWithInfo(scheduleId: Long): List<ScheduleParticipantInfoProjection>
}

interface ScheduleParticipantInfoProjection {
    val memberId: Long
    val name: String
    val profileImage: String?
    val participatedAt: LocalDateTime
}
