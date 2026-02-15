package io.lees.boom.storage.db.core.repository

import io.lees.boom.storage.db.core.entity.CrewEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CrewJpaRepository : JpaRepository<CrewEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CrewEntity c WHERE c.id = :crewId")
    fun findByIdForUpdate(
        @Param("crewId") crewId: Long,
    ): CrewEntity?

    @Modifying
    @Query("UPDATE CrewEntity c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.id = :crewId")
    fun softDelete(
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

    @Query(
        """
        SELECT c.id as crewId, c.name as name, c.description as description,
               (SELECT COUNT(cm2) FROM CrewMemberEntity cm2 WHERE cm2.crewId = c.id) as memberCount,
               c.maxMemberCount as maxMemberCount,
               COALESCE(AVG(m.activityScore), 0) as avgScore,
               c.latitude as latitude, c.longitude as longitude, c.address as address
        FROM CrewEntity c
        LEFT JOIN CrewMemberEntity cm ON c.id = cm.crewId
        LEFT JOIN MemberEntity m ON cm.memberId = m.id
        GROUP BY c.id, c.name, c.description, c.maxMemberCount, c.latitude, c.longitude, c.address
        ORDER BY avgScore DESC
        """,
    )
    fun findCrewRankingByAvgScore(pageable: Pageable): Slice<CrewRankingProjection>
}
