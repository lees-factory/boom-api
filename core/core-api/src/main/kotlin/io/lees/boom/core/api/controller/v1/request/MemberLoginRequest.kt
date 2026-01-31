package io.lees.boom.core.api.controller.v1.request

import io.lees.boom.core.enums.SocialProvider

data class MemberLoginRequest(
    val provider: SocialProvider, // JSON 파싱 시 String -> Enum 자동 매핑 (예: "KAKAO")
    val token: String, // 클라이언트(SDK)에서 받은 액세스 토큰 또는 ID 토큰
)
