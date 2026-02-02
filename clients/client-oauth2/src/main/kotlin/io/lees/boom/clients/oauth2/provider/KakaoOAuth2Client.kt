package io.lees.boom.clients.oauth2.provider

import com.fasterxml.jackson.annotation.JsonProperty
import io.lees.boom.clients.oauth2.InternalOAuth2UserInfo
import io.lees.boom.clients.oauth2.OAuth2Client
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class KakaoOAuth2Client : OAuth2Client {
    override val providerName: String = PROVIDER_NAME

    private val webClient: WebClient =
        WebClient
            .builder()
            .baseUrl(KAKAO_API_BASE_URL)
            .build()

    override fun getUserInfo(accessToken: String): InternalOAuth2UserInfo {
        val response =
            webClient
                .get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .bodyToMono(KakaoUserResponse::class.java)
                .block() ?: throw IllegalStateException("Failed to get Kakao user info")

        return InternalOAuth2UserInfo(
            socialId = response.id.toString(),
            email = response.kakaoAccount?.email ?: "",
            name = response.kakaoAccount?.profile?.nickname ?: "Unknown",
        )
    }

    private data class KakaoUserResponse(
        val id: Long,
        @JsonProperty("kakao_account")
        val kakaoAccount: KakaoAccount?,
    ) {
        data class KakaoAccount(
            val email: String?,
            val profile: Profile?,
        ) {
            data class Profile(
                val nickname: String?,
            )
        }
    }

    companion object {
        const val PROVIDER_NAME = "KAKAO"
        private const val KAKAO_API_BASE_URL = "https://kapi.kakao.com"
    }
}
