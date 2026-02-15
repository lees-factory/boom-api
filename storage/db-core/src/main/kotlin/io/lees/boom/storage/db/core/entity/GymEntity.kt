package io.lees.boom.storage.db.core.entity

import io.lees.boom.core.enums.CrowdLevel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(name = "gym", indexes = [Index(name = "idx_gym_location", columnList = "location,longitude")])
class GymEntity(
    var name: String,
    var address: String? = null,
    var latitude: Double,
    var longitude: Double,
    @Column(nullable = false)
    var maxCapacity: Int = 50, // [추가] 최대 수용 인원 (기본 50)
    @Column(nullable = false)
    var currentCount: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var crowdLevel: CrowdLevel,
) : BaseEntity() {
    fun update(
        name: String,
        address: String?,
        latitude: Double,
        longitude: Double,
        currentCount: Int,
        crowdLevel: CrowdLevel,
    ) {
        this.name = name
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.currentCount = currentCount
        this.crowdLevel = crowdLevel
    }

    fun updateCrowdStatus(
        newCount: Int,
        newLevel: CrowdLevel,
    ) {
        this.currentCount = newCount
        this.crowdLevel = newLevel
    }
}
