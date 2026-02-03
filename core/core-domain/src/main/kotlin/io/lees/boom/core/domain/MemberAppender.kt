package io.lees.boom.core.domain

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class MemberAppender(
    private val memberRepository: MemberRepository,
) {
    // 여기가 트랜잭션의 경계입니다. Service 대신 여기서 트랜잭션을 엽니다.
    @Transactional
    fun append(member: Member): Member = memberRepository.save(member)
}
