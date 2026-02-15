package io.lees.boom.core.domain.report

import io.lees.boom.core.enums.ReportReason
import io.lees.boom.core.enums.ReportTargetType
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val reportRepository: ReportRepository,
) {
    fun createReport(
        reporterId: Long,
        targetType: ReportTargetType,
        targetId: Long,
        reason: ReportReason,
        description: String?,
    ): Report {
        val report = Report.create(reporterId, targetType, targetId, reason, description)
        return reportRepository.save(report)
    }
}
