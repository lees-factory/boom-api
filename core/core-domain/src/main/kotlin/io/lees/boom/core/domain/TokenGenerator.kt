package io.lees.boom.core.domain

interface TokenGenerator {
    fun createAccessToken(
        memberId: Long,
        role: String,
    ): String

    fun createRefreshToken(): String

    fun getRefreshTokenExpirationSeconds(): Long
}
