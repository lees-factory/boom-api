package io.lees.boom.core.domain.report

import org.springframework.stereotype.Component

@Component
class ReportWriter(
    private val reportRepository: ReportRepository,
) {
    fun deleteAllByReporterId(reporterId: Long) = reportRepository.deleteAllByReporterId(reporterId)
}
