package io.lees.boom.core.domain.member

import org.springframework.stereotype.Component

@Component
class MemberDeleter(
    private val memberRepository: MemberRepository,
) {
    fun delete(memberId: Long) = memberRepository.deleteById(memberId)
}
