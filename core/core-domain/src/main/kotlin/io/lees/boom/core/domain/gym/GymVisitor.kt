package io.lees.boom.core.domain.gym

import java.time.LocalDateTime

/**
 * 암장 입장 유저 정보 (GymVisit + Member 조인 결과)
 */
data class GymVisitor(
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
    val admittedAt: LocalDateTime,
)
