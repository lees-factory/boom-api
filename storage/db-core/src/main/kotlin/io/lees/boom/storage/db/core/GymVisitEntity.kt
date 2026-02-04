package io.lees.boom.storage.db.core

import io.lees.boom.core.enums.VisitStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "gym_visit")
class GymVisitEntity(
    @Column(nullable = false)
    val gymId: Long,
    @Column(nullable = false)
    val memberId: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: VisitStatus,
    @Column(nullable = false)
    val admittedAt: LocalDateTime,
    var exitedAt: LocalDateTime? = null,
) : BaseEntity() {
    fun exit(time: LocalDateTime) {
        this.status = VisitStatus.EXIT
        this.exitedAt = time
    }
}
