package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Component

@Component
class SocialProfileReader {
    fun read(
        provider: SocialProvider,
        token: String,
    ): SocialProfile =
        when (provider) {
            SocialProvider.GOOGLE -> readGoogleProfile(token)
            SocialProvider.APPLE -> readAppleProfile(token)
            SocialProvider.KAKAO -> readKakaoProfile(token)
        }

    private fun readGoogleProfile(token: String): SocialProfile {
        // ... implementation (http client 호출 등)
        return SocialProfile(SocialProvider.GOOGLE, "sub_g", "g@test.com", "GUser")
    }

    private fun readAppleProfile(token: String): SocialProfile =
        SocialProfile(SocialProvider.APPLE, "sub_a", "a@test.com", "AUser")

    private fun readKakaoProfile(token: String): SocialProfile =
        SocialProfile(SocialProvider.KAKAO, "id_k", "k@test.com", "KUser")
}
