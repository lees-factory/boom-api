package io.lees.boom.storage.db.core.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "crew_schedule")
class CrewScheduleEntity(
    val crewId: Long,
    val gymId: Long? = null,
    val title: String,
    val description: String,
    val scheduledAt: LocalDateTime,
    val createdBy: Long,
) : BaseEntity()
