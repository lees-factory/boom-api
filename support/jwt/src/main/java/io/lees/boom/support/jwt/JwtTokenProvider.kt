package io.lees.boom.support.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.lees.boom.core.domain.TokenGenerator
import io.lees.boom.support.jwt.config.JwtProperties
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey
import kotlin.getValue

@Component
class JwtTokenProvider(
    // 값 주입 방식은 @Value 또는 @ConfigurationProperties 등 상황에 맞게 사용
    private val jwtProperties: JwtProperties,
) : TokenGenerator {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    // 1. Access Token 생성 (기존 로직)
    override fun createAccessToken(
        memberId: Long,
        role: String,
    ): String {
        val now = Date()
        val validity = Date(now.time + (jwtProperties.accessTokenExpirationSeconds * 1000))

        return Jwts
            .builder()
            .subject(memberId.toString())
            .claim("role", role)
            .issuer(jwtProperties.issuer)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    // 2. [추가됨] Refresh Token 생성
    override fun createRefreshToken(): String {
        val now = Date()
        // 설정 파일에서 refresh-token-expiration-seconds (예: 2주) 가져옴
        val validity = Date(now.time + (jwtProperties.refreshTokenExpirationSeconds * 1000))

        return Jwts
            .builder()
            .issuer(jwtProperties.issuer)
            .issuedAt(now)
            .expiration(validity) // 유효기간 길게 설정
            .signWith(key) // 서명은 동일하게 하여 위조 방지
            .compact()
    }

    override fun getRefreshTokenExpirationSeconds(): Long = jwtProperties.refreshTokenExpirationSeconds
}
