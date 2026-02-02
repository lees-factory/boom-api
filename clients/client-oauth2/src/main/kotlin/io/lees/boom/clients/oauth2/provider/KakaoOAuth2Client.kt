package io.lees.boom.clients.oauth2.provider

import io.lees.boom.clients.oauth2.InternalOAuth2UserInfo
import io.lees.boom.clients.oauth2.OAuth2Client
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class KakaoOAuth2Client : OAuth2Client {
    override val providerName: String = PROVIDER_NAME

    // [변경 1] URL이 kapi.kakao.com -> kauth.kakao.com 으로 변경됨
    private val webClient: WebClient =
        WebClient
            .builder()
            .baseUrl("https://kauth.kakao.com")
            .build()

    override fun getUserInfo(token: String): InternalOAuth2UserInfo {
        // [변경 2] GET /v2/user/me 대신 -> POST /oauth/tokeninfo 사용
        // [변경 3] 헤더(Bearer) 대신 -> Form Data(id_token) 사용
        val response =
            webClient
                .post()
                .uri("/oauth/tokeninfo")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("id_token", token))
                .retrieve()
                .bodyToMono<KakaoIdTokenResponse>()
                .block() ?: throw IllegalStateException("Failed to verify Kakao id_token")

        return InternalOAuth2UserInfo(
            socialId = response.sub, // 회원번호 (sub)
            email = response.email ?: "", // 이메일
            name = response.nickname ?: "Unknown", // 닉네임
        )
    }

    // [변경 4] 응답 DTO를 ID Token 페이로드 형태에 맞게 수정
    private data class KakaoIdTokenResponse(
        val sub: String, // 회원번호
        val email: String?, // 이메일 (id_token에 포함되어 있음)
        val nickname: String?, // 닉네임
        val aud: String, // (선택) 앱 키 확인용
    )

    companion object {
        const val PROVIDER_NAME = "KAKAO"
    }
}
