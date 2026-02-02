package io.lees.boom.core.domain

/**
 * OAuth2 사용자 정보 조회 인터페이스 (Data Access Layer)
 * 외부 OAuth2 Provider 연동을 추상화
 * 구현체는 clients:client-oauth2 모듈에 존재
 */
interface OAuth2UserInfoReader {
    fun read(
        provider: String,
        token: String,
    ): OAuth2UserInfo
}

/**
 * OAuth2 사용자 정보 DTO
 */
data class OAuth2UserInfo(
    val socialId: String,
    val email: String,
    val name: String,
)
