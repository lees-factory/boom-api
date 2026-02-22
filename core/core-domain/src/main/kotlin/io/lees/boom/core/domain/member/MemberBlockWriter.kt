package io.lees.boom.core.domain.member

import org.springframework.stereotype.Component

@Component
class MemberBlockWriter(
    private val memberBlockRepository: MemberBlockRepository,
) {
    fun deleteAllByMemberId(memberId: Long) = memberBlockRepository.deleteAllByMemberId(memberId)
}
