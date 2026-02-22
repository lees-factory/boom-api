package io.lees.boom.storage.db.core.repository

import io.lees.boom.storage.db.core.entity.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReportJpaRepository : JpaRepository<ReportEntity, Long> {
    fun deleteAllByReporterId(reporterId: Long)
}
