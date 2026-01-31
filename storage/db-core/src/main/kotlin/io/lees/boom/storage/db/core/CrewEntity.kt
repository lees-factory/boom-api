package io.lees.boom.storage.db.core

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "crew")
class CrewEntity(
    val name: String,
    val description: String,
    val maxMemberCount: Int,
) : BaseEntity()
