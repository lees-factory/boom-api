package io.lees.boom.core.domain.auth

import java.time.LocalDateTime

data class MemberToken(
    val memberId: Long,
    val refreshToken: String,
    val expirationDateTime: LocalDateTime,
)
