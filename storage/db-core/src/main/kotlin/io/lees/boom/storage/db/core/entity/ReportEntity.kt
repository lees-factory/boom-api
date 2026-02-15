package io.lees.boom.storage.db.core.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "report")
class ReportEntity(
    val reporterId: Long,
    val targetType: String,
    val targetId: Long,
    val reason: String,
    val description: String? = null,
) : BaseEntity()
