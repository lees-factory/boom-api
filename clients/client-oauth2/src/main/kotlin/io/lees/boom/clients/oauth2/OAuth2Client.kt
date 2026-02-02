package io.lees.boom.clients.oauth2

/**
 * OAuth2 클라이언트 인터페이스
 * Data Access Layer 내부 - 외부 OAuth2 Provider와 통신
 */
interface OAuth2Client {
    val providerName: String

    fun getUserInfo(accessToken: String): InternalOAuth2UserInfo
}

/**
 * 내부 전송용 OAuth2 사용자 정보
 */
data class InternalOAuth2UserInfo(
    val socialId: String,
    val email: String,
    val name: String,
)
