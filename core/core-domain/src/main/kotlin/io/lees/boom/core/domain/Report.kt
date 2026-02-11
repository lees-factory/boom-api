package io.lees.boom.core.domain

import io.lees.boom.core.enums.ReportReason
import io.lees.boom.core.enums.ReportTargetType

data class Report(
    val id: Long? = null,
    val reporterId: Long,
    val targetType: ReportTargetType,
    val targetId: Long,
    val reason: ReportReason,
    val description: String? = null,
) {
    companion object {
        fun create(
            reporterId: Long,
            targetType: ReportTargetType,
            targetId: Long,
            reason: ReportReason,
            description: String?,
        ): Report =
            Report(
                reporterId = reporterId,
                targetType = targetType,
                targetId = targetId,
                reason = reason,
                description = description,
            )
    }
}
