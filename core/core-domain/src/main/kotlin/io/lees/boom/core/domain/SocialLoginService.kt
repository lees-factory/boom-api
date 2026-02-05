package io.lees.boom.core.domain

import io.lees.boom.core.enums.MemberRole
import io.lees.boom.core.enums.SocialProvider
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Service

@Service
class SocialLoginService(
    private val memberFinder: MemberFinder,
    private val memberAppender: MemberAppender,
    private val tokenGenerator: TokenGenerator,
    private val refreshTokenStore: RefreshTokenStore,
    private val refreshTokenReader: RefreshTokenReader,
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
                // 2. 저장
                memberAppender.append(newMember)
            }

        // [수정] ID 검증 로직 추가 (!! 제거)
        // DB에 저장된 객체인데 ID가 없다면 시스템 에러로 간주합니다.
        val memberId = targetMember.id ?: throw CoreException(CoreErrorType.INVALID_USERID)

        // 3. 토큰 발급 (이제 memberId는 Long 타입이므로 에러 없음)
        val accessToken = tokenGenerator.createAccessToken(memberId, targetMember.role.name)
        val refreshToken = tokenGenerator.createRefreshToken()

        // 4. Refresh Token 저장
        refreshTokenStore.store(
            memberId = memberId,
            refreshToken = refreshToken,
            ttlSeconds = tokenGenerator.getRefreshTokenExpirationSeconds(),
        )

        return TokenPair(accessToken, refreshToken)
    }

    fun refreshToken(inputRefreshToken: String): TokenPair {
        val memberToken = refreshTokenReader.readByToken(inputRefreshToken)
            ?: throw CoreException(CoreErrorType.UNAUTHORIZED_USER)

        val member = memberFinder.findById(memberToken.memberId)
            ?: throw CoreException(CoreErrorType.NOT_FOUND_MEMBER)

        val memberId = member.id
            ?: throw CoreException(CoreErrorType.INVALID_USERID)

        val newAccessToken = tokenGenerator.createAccessToken(memberId, member.role.name)
        val newRefreshToken = tokenGenerator.createRefreshToken()

        refreshTokenStore.store(
            memberId = memberId,
            refreshToken = newRefreshToken,
            ttlSeconds = tokenGenerator.getRefreshTokenExpirationSeconds(),
        )

        return TokenPair(newAccessToken, newRefreshToken)
    }
}
