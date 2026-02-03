package io.lees.boom.core.domain

import org.springframework.stereotype.Component

@Component
class RefreshTokenReader(
    private val memberTokenRepository: MemberTokenRepository,
) {
    fun read(memberId: Long): MemberToken? = memberTokenRepository.findByMemberId(memberId)
}
