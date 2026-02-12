package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface MemberNotificationSettingJpaRepository : JpaRepository<MemberNotificationSettingEntity, Long> {
    fun findByMemberId(memberId: Long): MemberNotificationSettingEntity?
}
