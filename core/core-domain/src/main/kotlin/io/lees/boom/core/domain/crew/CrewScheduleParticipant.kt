package io.lees.boom.core.domain.crew

import java.time.LocalDateTime

data class CrewScheduleParticipant(
    val id: Long? = null,
    val scheduleId: Long,
    val memberId: Long,
    val participatedAt: LocalDateTime,
) {
    companion object {
        fun create(
            scheduleId: Long,
            memberId: Long,
        ): CrewScheduleParticipant =
            CrewScheduleParticipant(
                scheduleId = scheduleId,
                memberId = memberId,
                participatedAt = LocalDateTime.now(),
            )
    }
}
