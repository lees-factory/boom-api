package io.lees.boom.core.domain

interface ReportRepository {
    fun save(report: Report): Report
}
