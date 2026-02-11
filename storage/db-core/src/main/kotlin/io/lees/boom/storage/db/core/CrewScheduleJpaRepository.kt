package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CrewScheduleJpaRepository : JpaRepository<CrewScheduleEntity, Long> {
    fun findByCrewIdOrderByScheduledAtAsc(crewId: Long): List<CrewScheduleEntity>

    @Query(
        """
        SELECT s.id as id, s.crewId as crewId, s.gymId as gymId,
               s.title as title, s.description as description,
               s.scheduledAt as scheduledAt, s.createdBy as createdBy,
               m.name as createdByName
        FROM CrewScheduleEntity s
        JOIN MemberEntity m ON s.createdBy = m.id
        WHERE s.crewId = :crewId
        ORDER BY s.scheduledAt ASC
        """,
    )
    fun findWithCreatorByCrewId(crewId: Long): List<ScheduleWithCreatorProjection>
}

interface ScheduleWithCreatorProjection {
    val id: Long
    val crewId: Long
    val gymId: Long?
    val title: String
    val description: String
    val scheduledAt: java.time.LocalDateTime
    val createdBy: Long
    val createdByName: String
}
