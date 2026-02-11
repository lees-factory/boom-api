package io.lees.boom.core.api.controller.v1.request

import io.lees.boom.core.enums.ReportReason
import io.lees.boom.core.enums.ReportTargetType

data class ReportCreateRequest(
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: ReportReason,
    val description: String? = null,
)
