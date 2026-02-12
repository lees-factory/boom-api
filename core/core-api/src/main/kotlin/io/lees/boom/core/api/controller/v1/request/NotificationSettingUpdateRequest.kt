package io.lees.boom.core.api.controller.v1.request

data class NotificationSettingUpdateRequest(
    val pushEnabled: Boolean,
    val crewScheduleEnabled: Boolean,
    val crewChatEnabled: Boolean,
)
