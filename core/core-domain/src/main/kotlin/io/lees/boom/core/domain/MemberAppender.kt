package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class MemberAppender(
    private val memberRepository: MemberRepository
) {
    suspend fun append(member: Member): Member {
        return memberRepository.save(member)
    }
}