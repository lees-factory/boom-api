package io.lees.boom.storage.db.core.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "member_badge")
class MemberBadgeEntity(
    @Column(nullable = false)
    val memberId: Long,
    @Column(nullable = false)
    val badgeType: String,
    @Column(nullable = false)
    val acquiredAt: LocalDateTime,
    @Column(nullable = false)
    val notified: Boolean = false,
) : BaseEntity()
