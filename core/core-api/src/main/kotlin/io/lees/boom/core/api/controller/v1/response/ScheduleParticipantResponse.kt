package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CrewScheduleParticipantInfo
import java.time.LocalDateTime

data class ScheduleParticipantResponse(
    val memberId: Long,
    val name: String,
    val profileImage: String?,
    val participatedAt: LocalDateTime,
) {
    companion object {
        fun from(info: CrewScheduleParticipantInfo): ScheduleParticipantResponse =
            ScheduleParticipantResponse(
                memberId = info.memberId,
                name = info.name,
                profileImage = info.profileImage,
                participatedAt = info.participatedAt,
            )
    }
}
