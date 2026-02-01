package io.lees.boom.storage.db.core

import io.lees.boom.core.enums.CrowdLevel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(name="gym", indexes = [Index(name="idx_gym_location",columnList="location,longitude")])
class GymEntity (
    var name: String,
    var address: String? = null,
    var latitude: Double,
    var longitude: Double,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var crowdLevel: CrowdLevel,

    ): BaseEntity(){
    fun update(name: String, address: String?, latitude: Double, longitude: Double, crowdLevel: CrowdLevel) {
        this.name = name
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.crowdLevel = crowdLevel
    }
}
