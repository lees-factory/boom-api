package io.lees.boom.core.domain.member

import org.springframework.stereotype.Component

@Component
class MemberNotificationSettingWriter(
    private val memberNotificationSettingRepository: MemberNotificationSettingRepository,
) {
    fun deleteByMemberId(memberId: Long) = memberNotificationSettingRepository.deleteByMemberId(memberId)
}
