package io.lees.boom.core.domain

data class MemberNotificationSetting(
    val id: Long? = null,
    val memberId: Long,
    val pushToken: String? = null,
    val pushEnabled: Boolean = true,
    val crewScheduleEnabled: Boolean = true,
    val crewChatEnabled: Boolean = true,
) {
    companion object {
        fun create(memberId: Long): MemberNotificationSetting =
            MemberNotificationSetting(
                memberId = memberId,
            )
    }
}
