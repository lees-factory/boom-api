package io.lees.boom.support.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.lees.boom.core.domain.TokenGenerator
import io.lees.boom.support.jwt.config.JwtProperties
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
) : TokenGenerator { // 도메인의 인터페이스 구현

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    override fun generate(
        memberId: Long,
        role: String,
    ): String {
        val now = Date()
        val validity = Date(now.time + (jwtProperties.expirationSeconds * 1000))

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
}
