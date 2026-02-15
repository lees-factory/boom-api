package io.lees.boom.core.domain.auth

interface TokenGenerator {
    fun createAccessToken(
        memberId: Long,
        role: String,
    ): String

    // [수정] memberId를 선택적으로 받거나 필수로 변경 권장.
    // 여기서는 기존 코드 호환성을 위해 유지하되, 구현체에서 확장 사용
    fun createRefreshToken(): String

    // Kotlin의 Default Arguments는 인터페이스 메서드 오버라이드 시 문제될 수 있으므로
    // 별도 메서드나 구현체 형변환을 사용해야 하는데,
    // 깔끔하게 설계를 변경하자면 아래처럼 되어야 합니다.
    // fun createRefreshToken(memberId: Long): String

    fun getRefreshTokenExpirationSeconds(): Long
}
