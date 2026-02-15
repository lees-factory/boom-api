package io.lees.boom.storage.db.core.repository

import io.lees.boom.storage.db.core.entity.GymEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GymJpaRepository : JpaRepository<GymEntity, Long> {
    fun findByLatitudeBetweenAndLongitudeBetween(
        minimumLatitude: Double,
        maximumLatitude: Double,
        minimumLongitude: Double,
        maximumLongitude: Double,
    ): List<GymEntity>

    @Query(
        """
        SELECT g FROM GymEntity g
        WHERE g.latitude BETWEEN :minLat AND :maxLat
        AND g.longitude BETWEEN :minLon AND :maxLon
        ORDER BY g.id ASC
        """,
    )
    fun findByViewportWithLimit(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double,
        pageable: Pageable,
    ): List<GymEntity>
}
