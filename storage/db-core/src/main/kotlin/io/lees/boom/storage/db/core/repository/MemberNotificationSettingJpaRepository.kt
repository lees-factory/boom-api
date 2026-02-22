package io.lees.boom.storage.db.core.repository

import io.lees.boom.storage.db.core.entity.MemberNotificationSettingEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberNotificationSettingJpaRepository : JpaRepository<MemberNotificationSettingEntity, Long> {
    fun findByMemberId(memberId: Long): MemberNotificationSettingEntity?

    fun deleteByMemberId(memberId: Long)
}
