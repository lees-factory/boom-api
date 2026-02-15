package io.lees.boom.storage.db.core.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "gym_active_visit",
    indexes = [
        Index(name = "idx_active_visit_member", columnList = "memberId", unique = true),
        Index(name = "idx_active_visit_gym", columnList = "gymId"),
        Index(name = "idx_active_visit_expires", columnList = "expiresAt"),
    ],
)
class GymActiveVisitEntity(
    @Column(nullable = false)
    val gymId: Long,
    @Column(nullable = false, unique = true)
    val memberId: Long,
    @Column(nullable = false)
    val admittedAt: LocalDateTime,
    @Column(nullable = false)
    var expiresAt: LocalDateTime,
) : BaseEntity() {
    fun extend(newExpiresAt: LocalDateTime) {
        this.expiresAt = newExpiresAt
    }
}
