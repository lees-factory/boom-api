package io.lees.boom.storage.db.core

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "crew")
@SQLRestriction("deleted_at IS NULL")
class CrewEntity(
    val name: String,
    val description: String,
    var crewImage: String? = null,
    var maxMemberCount: Int,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var address: String? = null,
    var activityScore: Double = 0.0,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity()
