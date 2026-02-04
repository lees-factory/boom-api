package io.lees.boom.core.domain

import io.lees.boom.core.enums.VisitStatus
import java.time.LocalDateTime

data class GymVisit(
    val id: Long? = null,
    val gymId: Long,
    val memberId: Long,
    val status: VisitStatus,
    val admittedAt: LocalDateTime,
    val exitedAt: LocalDateTime? = null,
) {
    companion object {
        fun createAdmission(
            gymId: Long,
            memberId: Long,
        ): GymVisit =
            GymVisit(
                gymId = gymId,
                memberId = memberId,
                status = VisitStatus.ADMISSION,
                admittedAt = LocalDateTime.now(),
            )
    }

    fun exit(): GymVisit =
        this.copy(
            status = VisitStatus.EXIT,
            exitedAt = LocalDateTime.now(),
        )
}
