package io.lees.boom.storage.db.core

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CrewJpaRepository : JpaRepository<CrewEntity, Long> {
    @Modifying
    @Query("UPDATE CrewEntity c SET c.memberCount = c.memberCount + 1 WHERE c.id = :crewId")
    fun incrementMemberCount(
        @Param("crewId") crewId: Long,
    )

    @Query(
        """
        SELECT c FROM CrewEntity c
        WHERE c.latitude BETWEEN :minLat AND :maxLat
        AND c.longitude BETWEEN :minLon AND :maxLon
        ORDER BY c.activityScore DESC
        """,
    )
    fun findByLocation(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double,
        pageable: Pageable,
    ): Slice<CrewEntity>

    fun findByOrderByActivityScoreDesc(pageable: Pageable): Slice<CrewEntity>
}
