package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface GymJpaRepository : JpaRepository<GymEntity, Long> {

    fun findByLatitudeBetweenAndLongitudeBetween(
        minimumLatitude: Double,
        maximumLatitude: Double,
        minimumLongitude: Double,
        maximumLongitude: Double,
    ): List<GymEntity>
}
