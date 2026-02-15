package io.lees.boom.core.domain.gym

import io.lees.boom.core.enums.VisitStatus
import java.time.LocalDateTime

data class GymVisit(
    val id: Long? = null,
    val gymId: Long,
    val memberId: Long,
    val status: VisitStatus,
    val admittedAt: LocalDateTime,
    val exitedAt: LocalDateTime? = null,
    val expiresAt: LocalDateTime, // 만료 예정 시간
) {
    companion object {
        const val DEFAULT_DURATION_HOURS = 3L

        fun createAdmission(
            gymId: Long,
            memberId: Long,
            durationHours: Long = DEFAULT_DURATION_HOURS,
        ): GymVisit {
            val now = LocalDateTime.now()
            return GymVisit(
                gymId = gymId,
                memberId = memberId,
                status = VisitStatus.ADMISSION,
                admittedAt = now,
                expiresAt = now.plusHours(durationHours),
            )
        }
    }

    /**
     * 퇴장 처리
     */
    fun exit(): GymVisit =
        this.copy(
            status = VisitStatus.EXIT,
            exitedAt = LocalDateTime.now(),
        )

    /**
     * 방문 연장 (기본 3시간 추가)
     */
    fun extend(additionalHours: Long = DEFAULT_DURATION_HOURS): GymVisit =
        this.copy(
            expiresAt = this.expiresAt.plusHours(additionalHours),
        )

    /**
     * 만료 여부 확인
     */
    fun isExpired(now: LocalDateTime = LocalDateTime.now()): Boolean =
        status == VisitStatus.ADMISSION && now.isAfter(expiresAt)
}
