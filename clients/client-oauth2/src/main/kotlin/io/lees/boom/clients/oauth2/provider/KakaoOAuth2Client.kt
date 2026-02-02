package io.lees.boom.clients.oauth2.provider

import io.lees.boom.clients.oauth2.InternalOAuth2UserInfo
import io.lees.boom.clients.oauth2.OAuth2Client
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class KakaoOAuth2Client : OAuth2Client {

    override val providerName: String = PROVIDER_NAME

    // [설정] OIDC UserInfo API는 'kapi.kakao.com'을 사용합니다.
    private val webClient: WebClient =
        WebClient
            .builder()
            .baseUrl("https://kapi.kakao.com")
            .build()

    override fun getUserInfo(accessToken: String): InternalOAuth2UserInfo {
        // [변경] GET /v1/oidc/userinfo 호출
        // 헤더에 "Bearer {Access Token}"을 넣어서 보냅니다.
        val response =
            webClient
                .get()
                .uri("/v1/oidc/userinfo")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .bodyToMono<KakaoOidcResponse>()
                .block() ?: throw IllegalStateException("Failed to get Kakao OIDC user info")

        return InternalOAuth2UserInfo(
            socialId = response.sub, // 회원번호
            email = response.email ?: "", // 이메일 (동의항목 설정 필요)
            name = response.nickname ?: "Unknown", // 닉네임
        )
    }

    // [응답 DTO] OIDC 스펙에 맞춘 평평한(Flat) 구조
    private data class KakaoOidcResponse(
        val sub: String, // 회원번호 (Subject)
        val nickname: String?, // 닉네임
        val email: String?, // 이메일
        val picture: String?, // 프로필 사진 (필요시 사용)
    )

    companion object {
        const val PROVIDER_NAME = "KAKAO"
    }


    //    override val providerName: String = PROVIDER_NAME
//
//    // [변경 1] URL이 kapi.kakao.com -> kauth.kakao.com 으로 변경됨
//    private val webClient: WebClient =
//        WebClient
//            .builder()
//            .baseUrl("https://kauth.kakao.com")
//            .build()
//
//    override fun getUserInfo(token: String): InternalOAuth2UserInfo {
//        // [변경 2] GET /v2/user/me 대신 -> POST /oauth/tokeninfo 사용
//        // [변경 3] 헤더(Bearer) 대신 -> Form Data(id_token) 사용
//        val response =
//            webClient
//                .post()
//                .uri("/oauth/tokeninfo")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .body(BodyInserters.fromFormData("id_token", token))
//                .retrieve()
//                .bodyToMono<KakaoIdTokenResponse>()
//                .block() ?: throw IllegalStateException("Failed to verify Kakao id_token")
//
//        return InternalOAuth2UserInfo(
//            socialId = response.sub, // 회원번호 (sub)
//            email = response.email ?: "", // 이메일
//            name = response.nickname ?: "Unknown", // 닉네임
//        )
//    }
//
//    // [변경 4] 응답 DTO를 ID Token 페이로드 형태에 맞게 수정
//    private data class KakaoIdTokenResponse(
//        val sub: String, // 회원번호
//        val email: String?, // 이메일 (id_token에 포함되어 있음)
//        val nickname: String?, // 닉네임
//        val aud: String, // (선택) 앱 키 확인용
//    )
//
//    companion object {
//        const val PROVIDER_NAME = "KAKAO"
//    }
}
