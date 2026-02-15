package io.lees.boom.core.domain.auth

import org.springframework.stereotype.Component

@Component
class RefreshTokenReader(
    private val memberTokenRepository: MemberTokenRepository,
) {
    fun read(memberId: Long): MemberToken? = memberTokenRepository.findByMemberId(memberId)

    // [추가] 토큰 값으로 읽기
    fun readByToken(refreshToken: String): MemberToken? = memberTokenRepository.findByRefreshToken(refreshToken)
}
