package io.lees.boom.clients.oauth2.provider

import io.lees.boom.clients.oauth2.InternalOAuth2UserInfo
import io.lees.boom.clients.oauth2.OAuth2Client
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GoogleOAuth2Client : OAuth2Client {
    override val providerName: String = PROVIDER_NAME

    private val webClient: WebClient =
        WebClient
            .builder()
            .baseUrl(GOOGLE_API_BASE_URL)
            .build()

    override fun getUserInfo(accessToken: String): InternalOAuth2UserInfo {
        val response =
            webClient
                .get()
                .uri("/oauth2/v3/userinfo")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .bodyToMono(GoogleUserResponse::class.java)
                .block() ?: throw IllegalStateException("Failed to get Google user info")

        return InternalOAuth2UserInfo(
            socialId = response.sub,
            email = response.email ?: "",
            name = response.name ?: "Unknown",
        )
    }

    private data class GoogleUserResponse(
        val sub: String,
        val email: String?,
        val name: String?,
    )

    companion object {
        const val PROVIDER_NAME = "GOOGLE"
        private const val GOOGLE_API_BASE_URL = "https://www.googleapis.com"
    }
}
