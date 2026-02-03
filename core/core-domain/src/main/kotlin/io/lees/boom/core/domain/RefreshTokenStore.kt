package io.lees.boom.core.domain

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RefreshTokenStore(
    private val memberTokenRepository: MemberTokenRepository,
) {
    fun store(
        memberId: Long,
        refreshToken: String,
        ttlSeconds: Long,
    ) {
        // 도메인 객체 생성
        val memberToken =
            MemberToken(
                memberId = memberId,
                refreshToken = refreshToken,
                expirationDateTime = LocalDateTime.now().plusSeconds(ttlSeconds),
            )

        memberTokenRepository.save(memberToken)
    }
}
