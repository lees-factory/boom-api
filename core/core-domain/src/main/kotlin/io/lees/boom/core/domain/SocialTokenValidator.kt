package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Component

@Component
class SocialTokenValidator() {

    suspend fun validate(provider: SocialProvider, token: String): SocialProfile {
        // 격벽: 각 공급자의 구현 상세는 이 도구 안에서만 캡슐화됩니다.
        return when (provider) {
            SocialProvider.GOOGLE -> verifyGoogle(token)
            SocialProvider.APPLE -> verifyApple(token)
            SocialProvider.KAKAO -> verifyKakao(token)
        }
    }

    private suspend fun verifyGoogle(token: String): SocialProfile {
        // 실제 구현: JWK 검증 및 Payload 파싱
        // val payload = googleClient.verify(token)
        return SocialProfile(SocialProvider.GOOGLE, "google_sub_123", "test@gmail.com", "GoogleUser")
    }

    private suspend fun verifyApple(token: String): SocialProfile {
        // 실제 구현: Apple ID Token 검증
        return SocialProfile(SocialProvider.APPLE, "apple_sub_456", "apple@icloud.com", "AppleUser")
    }

    private suspend fun verifyKakao(token: String): SocialProfile {
        // 실제 구현: Kakao Access Token으로 사용자 정보 조회
        return SocialProfile(SocialProvider.KAKAO, "kakao_id_789", "kakao@daum.net", "KakaoUser")
    }

}