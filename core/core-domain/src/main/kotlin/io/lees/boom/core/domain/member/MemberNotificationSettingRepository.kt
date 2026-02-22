package io.lees.boom.core.domain.member

interface MemberNotificationSettingRepository {
    fun findByMemberId(memberId: Long): MemberNotificationSetting?

    fun save(setting: MemberNotificationSetting): MemberNotificationSetting

    fun update(setting: MemberNotificationSetting)

    fun deleteByMemberId(memberId: Long)
}
