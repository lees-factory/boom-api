package io.lees.boom.support.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.lees.boom.core.domain.auth.TokenGenerator
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.support.jwt.config.JwtProperties
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
) : TokenGenerator {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8))
    }

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

    override fun createRefreshToken(): String {
        val now = Date()
        val validity = Date(now.time + (jwtProperties.refreshTokenExpirationSeconds * 1000))

        return Jwts
            .builder()
            .issuer(jwtProperties.issuer)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    override fun getRefreshTokenExpirationSeconds(): Long = jwtProperties.refreshTokenExpirationSeconds

    fun validateAndGetClaims(token: String): Claims =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            throw CoreException(CoreErrorType.UNAUTHORIZED_USER)
        }
}
