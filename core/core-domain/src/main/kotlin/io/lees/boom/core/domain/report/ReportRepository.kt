package io.lees.boom.core.domain.report

interface ReportRepository {
    fun save(report: Report): Report
}
