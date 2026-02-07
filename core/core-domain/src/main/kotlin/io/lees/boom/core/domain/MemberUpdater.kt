package io.lees.boom.core.domain

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class MemberUpdater(
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun update(member: Member): Member = memberRepository.update(member)
}
