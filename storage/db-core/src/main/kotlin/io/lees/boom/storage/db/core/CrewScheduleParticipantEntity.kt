package io.lees.boom.storage.db.core

import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "crew_schedule_participant")
class CrewScheduleParticipantEntity(
    val scheduleId: Long,
    val memberId: Long,
    val participatedAt: LocalDateTime,
) : BaseEntity()
