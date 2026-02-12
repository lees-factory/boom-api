package io.lees.boom.core.domain

interface MemberNotificationSettingRepository {
    fun findByMemberId(memberId: Long): MemberNotificationSetting?

    fun save(setting: MemberNotificationSetting): MemberNotificationSetting

    fun update(setting: MemberNotificationSetting)
}
