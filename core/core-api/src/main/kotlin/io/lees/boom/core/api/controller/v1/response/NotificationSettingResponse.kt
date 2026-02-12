package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.MemberNotificationSetting

data class NotificationSettingResponse(
    val pushEnabled: Boolean,
    val crewScheduleEnabled: Boolean,
    val crewChatEnabled: Boolean,
) {
    companion object {
        fun from(setting: MemberNotificationSetting): NotificationSettingResponse =
            NotificationSettingResponse(
                pushEnabled = setting.pushEnabled,
                crewScheduleEnabled = setting.crewScheduleEnabled,
                crewChatEnabled = setting.crewChatEnabled,
            )
    }
}
