package io.lees.boom.core.domain.badge

import org.springframework.stereotype.Component

@Component
class MemberBadgeWriter(
    private val memberBadgeRepository: MemberBadgeRepository,
) {
    fun deleteAllByMemberId(memberId: Long) = memberBadgeRepository.deleteAllByMemberId(memberId)
}
