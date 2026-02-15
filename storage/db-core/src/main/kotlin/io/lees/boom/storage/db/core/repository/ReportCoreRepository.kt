package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.report.Report
import io.lees.boom.core.domain.report.ReportRepository
import io.lees.boom.core.enums.ReportReason
import io.lees.boom.core.enums.ReportTargetType
import io.lees.boom.storage.db.core.entity.ReportEntity
import org.springframework.stereotype.Repository

@Repository
internal class ReportCoreRepository(
    private val reportJpaRepository: ReportJpaRepository,
) : ReportRepository {
    override fun save(report: Report): Report = reportJpaRepository.save(report.toEntity()).toDomain()

    private fun Report.toEntity() =
        ReportEntity(
            reporterId = this.reporterId,
            targetType = this.targetType.name,
            targetId = this.targetId,
            reason = this.reason.name,
            description = this.description,
        )

    private fun ReportEntity.toDomain() =
        Report(
            id = this.id,
            reporterId = this.reporterId,
            targetType = ReportTargetType.valueOf(this.targetType),
            targetId = this.targetId,
            reason = ReportReason.valueOf(this.reason),
            description = this.description,
        )
}
