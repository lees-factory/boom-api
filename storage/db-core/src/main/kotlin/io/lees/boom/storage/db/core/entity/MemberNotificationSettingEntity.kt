package io.lees.boom.storage.db.core.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "member_notification_setting")
class MemberNotificationSettingEntity(
    @Column(nullable = false, unique = true)
    val memberId: Long,
    var pushToken: String? = null,
    @Column(nullable = false)
    var pushEnabled: Boolean = true,
    @Column(nullable = false)
    var crewScheduleEnabled: Boolean = true,
    @Column(nullable = false)
    var crewChatEnabled: Boolean = true,
) : BaseEntity()
