package io.lees.boom.core.api.controller.v1.request

import java.time.LocalDateTime

data class CrewScheduleCreateRequest(
    val gymId: Long? = null,
    val title: String,
    val description: String,
    val scheduledAt: LocalDateTime,
)
