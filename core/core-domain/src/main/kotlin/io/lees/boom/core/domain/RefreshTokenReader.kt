package io.lees.boom.core.domain

import org.springframework.stereotype.Component
@Component
class RefreshTokenReader(
    private val memberTokenRepository: MemberTokenRepository,
) {
    fun read(memberId: Long): MemberToken? {
        return memberTokenRepository.findByMemberId(memberId)
    }

    // [추가] 토큰 값으로 읽기
    fun readByToken(refreshToken: String): MemberToken? {
        return memberTokenRepository.findByRefreshToken(refreshToken)
    }
}
