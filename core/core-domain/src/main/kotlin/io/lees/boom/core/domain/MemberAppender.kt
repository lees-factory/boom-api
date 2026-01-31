package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class MemberAppender(
    private val memberRepository: MemberRepository,
) {
    fun append(member: Member): Member = memberRepository.save(member)
}
