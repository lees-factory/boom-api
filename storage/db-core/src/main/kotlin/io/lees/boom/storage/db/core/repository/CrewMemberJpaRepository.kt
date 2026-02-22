package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.enums.CrewRole
import io.lees.boom.storage.db.core.entity.CrewMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MyCrewProjection {
    val crewId: Long
    val name: String
    val description: String
    val crewImage: String?
    val maxMemberCount: Int
    val myRole: CrewRole
    val memberCount: Long
    val latitude: Double?
    val longitude: Double?
    val address: String?
}

interface CrewMemberInfoProjection {
    val memberId: Long
    val memberName: String
    val memberProfileImage: String?
    val role: CrewRole
}

interface CrewMemberJpaRepository : JpaRepository<CrewMemberEntity, Long> {
    fun findByMemberId(memberId: Long): List<CrewMemberEntity>

    fun findByCrewIdIn(crewIds: List<Long>): List<CrewMemberEntity>

    @Query(
        """
        SELECT c.id as crewId, c.name as name, c.description as description,
               c.crewImage as crewImage, c.maxMemberCount as maxMemberCount, cm.role as myRole,
               (SELECT COUNT(cm2) FROM CrewMemberEntity cm2 WHERE cm2.crewId = c.id) as memberCount,
               c.latitude as latitude, c.longitude as longitude, c.address as address
        FROM CrewMemberEntity cm
        JOIN CrewEntity c ON cm.crewId = c.id
        WHERE cm.memberId = :memberId
        """,
    )
    fun findMyCrews(memberId: Long): List<MyCrewProjection>

    @Query(
        """
        SELECT cm.memberId as memberId, m.name as memberName,
               m.profileImage as memberProfileImage, cm.role as role
        FROM CrewMemberEntity cm
        JOIN MemberEntity m ON cm.memberId = m.id
        WHERE cm.crewId = :crewId
        ORDER BY CASE cm.role WHEN 'LEADER' THEN 0 WHEN 'MEMBER' THEN 1 WHEN 'GUEST' THEN 2 END
        """,
    )
    fun findMembersWithInfoByCrewId(crewId: Long): List<CrewMemberInfoProjection>

    fun findByCrewIdAndMemberId(
        crewId: Long,
        memberId: Long,
    ): CrewMemberEntity?

    fun countByCrewId(crewId: Long): Long

    fun countByCrewIdAndRole(
        crewId: Long,
        role: CrewRole,
    ): Long

    @Modifying
    @Query(
        "UPDATE CrewMemberEntity cm SET cm.deletedAt = CURRENT_TIMESTAMP WHERE cm.crewId = :crewId AND cm.deletedAt IS NULL",
    )
    fun softDeleteByCrewId(
        @Param("crewId") crewId: Long,
    )

    @Modifying
    @Query("UPDATE CrewMemberEntity cm SET cm.deletedAt = CURRENT_TIMESTAMP WHERE cm.id = :id")
    fun softDeleteById(
        @Param("id") id: Long,
    )

    @Modifying
    @Query(
        "UPDATE CrewMemberEntity cm SET cm.deletedAt = CURRENT_TIMESTAMP WHERE cm.memberId = :memberId AND cm.deletedAt IS NULL",
    )
    fun softDeleteByMemberId(
        @Param("memberId") memberId: Long,
    )
}
