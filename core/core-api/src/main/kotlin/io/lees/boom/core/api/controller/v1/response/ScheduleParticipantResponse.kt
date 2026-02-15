package io.lees.boom.core.api.controller.v1.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.lees.boom.core.domain.crew.CrewScheduleParticipantInfo
import java.time.LocalDateTime

data class ScheduleParticipantResponse(
    val memberId: Long,
    val name: String,
    val profileImage: String?,
    val participatedAt: LocalDateTime,
    @get:JsonProperty("isCreator")
    val isCreator: Boolean,
) {
    companion object {
        fun from(info: CrewScheduleParticipantInfo): ScheduleParticipantResponse =
            ScheduleParticipantResponse(
                memberId = info.memberId,
                name = info.name,
                profileImage = info.profileImage,
                participatedAt = info.participatedAt,
                isCreator = info.isCreator,
            )
    }
}
