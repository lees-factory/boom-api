package io.lees.boom.core.domain.auth

interface MemberTokenRepository {
    fun store(memberToken: MemberToken): MemberToken

    // [추가] 토큰 값으로 MemberToken 조회
    fun findByRefreshToken(refreshToken: String): MemberToken?

    fun findByMemberId(memberId: Long): MemberToken?
}
