package io.lees.boom.core.domain

import io.lees.boom.core.enums.MemberRole
import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Service

@Service
class SocialLoginService(
    private val memberFinder: MemberFinder,
    private val memberAppender: MemberAppender,
    private val tokenGenerator: TokenGenerator,
    private val refreshTokenStore: RefreshTokenStore,
) {
    fun login(
        provider: SocialProvider,
        socialId: String,
        name: String,
        email: String?,
        profileImage: String?,
    ): TokenPair {
        // 1. 조회
        val member = memberFinder.findBySocialInfo(provider, socialId)

        val targetMember =
            member ?: run {
                val newMember =
                    Member.register(
                        name = name,
                        email = email ?: "",
                        profileImage = profileImage,
                        role = MemberRole.USER,
                        provider = provider,
                        socialId = socialId,
                    )
                // 2. 저장 (Appender가 트랜잭션 관리)
                memberAppender.append(newMember)
            }

        // 3. 토큰 발급
        val accessToken = tokenGenerator.createAccessToken(targetMember.id!!, targetMember.role.name)
        val refreshToken = tokenGenerator.createRefreshToken()

        // 4. Refresh Token 저장 (하드코딩 제거됨!)
        // "토큰 생성기야, 이거 유효기간 몇 초니?" 라고 물어보고 저장
        refreshTokenStore.store(
            memberId = targetMember.id!!,
            refreshToken = refreshToken,
            ttlSeconds = tokenGenerator.getRefreshTokenExpirationSeconds(),
        )

        return TokenPair(accessToken, refreshToken)
    }
}
