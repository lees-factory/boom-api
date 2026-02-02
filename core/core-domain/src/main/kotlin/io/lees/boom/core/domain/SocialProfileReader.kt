package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Component

/**
 * 소셜 프로필 조회 도구 (Implement Layer)
 * 외부 OAuth2 연동을 통해 사용자 프로필을 조회하는 도구 클래스
 */
@Component
class SocialProfileReader(
    private val oAuth2UserInfoReader: OAuth2UserInfoReader,
) {
    fun read(
        provider: SocialProvider,
        token: String,
    ): SocialProfile {
        val userInfo = oAuth2UserInfoReader.read(provider.name, token)

        return SocialProfile(
            provider = provider,
            socialId = userInfo.socialId,
            email = userInfo.email,
            name = userInfo.name,
        )
    }
}
