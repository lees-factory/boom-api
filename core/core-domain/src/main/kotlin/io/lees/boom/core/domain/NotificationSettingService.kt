package io.lees.boom.core.domain

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class NotificationSettingService(
    private val memberNotificationSettingRepository: MemberNotificationSettingRepository,
) {
    @Transactional
    fun getOrCreateSetting(memberId: Long): MemberNotificationSetting {
        val existing = memberNotificationSettingRepository.findByMemberId(memberId)
        if (existing != null) return existing

        val setting = MemberNotificationSetting.create(memberId)
        return memberNotificationSettingRepository.save(setting)
    }

    @Transactional
    fun updatePushToken(
        memberId: Long,
        pushToken: String,
    ) {
        val setting = getOrCreateSetting(memberId)
        memberNotificationSettingRepository.update(
            setting.copy(pushToken = pushToken),
        )
    }

    @Transactional
    fun updateSetting(
        memberId: Long,
        pushEnabled: Boolean,
        crewScheduleEnabled: Boolean,
        crewChatEnabled: Boolean,
    ) {
        val setting = getOrCreateSetting(memberId)
        memberNotificationSettingRepository.update(
            setting.copy(
                pushEnabled = pushEnabled,
                crewScheduleEnabled = crewScheduleEnabled,
                crewChatEnabled = crewChatEnabled,
            ),
        )
    }
}
