package io.lees.boom.core.domain.gym

import java.time.LocalDateTime

/**
 * 현재 입장 상태 (유저당 최대 1개)
 * - 입장 시 UPSERT
 * - 퇴장 시 DELETE
 */
data class GymActiveVisit(
    val id: Long? = null,
    val gymId: Long,
    val memberId: Long,
    val admittedAt: LocalDateTime,
    val expiresAt: LocalDateTime,
) {
    companion object {
        const val DEFAULT_DURATION_HOURS = 3L

        fun create(
            gymId: Long,
            memberId: Long,
            durationHours: Long = DEFAULT_DURATION_HOURS,
        ): GymActiveVisit {
            val now = LocalDateTime.now()
            return GymActiveVisit(
                gymId = gymId,
                memberId = memberId,
                admittedAt = now,
                expiresAt = now.plusHours(durationHours),
            )
        }
    }

    /**
     * 방문 연장 (기본 3시간 추가)
     */
    fun extend(additionalHours: Long = DEFAULT_DURATION_HOURS): GymActiveVisit =
        this.copy(
            expiresAt = this.expiresAt.plusHours(additionalHours),
        )

    /**
     * 만료 여부 확인
     */
    fun isExpired(now: LocalDateTime = LocalDateTime.now()): Boolean = now.isAfter(expiresAt)
}
