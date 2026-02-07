package io.lees.boom.storage.db.core

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "crew")
class CrewEntity(
    val name: String,
    val description: String,
    val maxMemberCount: Int,
    var memberCount: Int = 0,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var address: String? = null,
    var activityScore: Double = 0.0,
) : BaseEntity()
