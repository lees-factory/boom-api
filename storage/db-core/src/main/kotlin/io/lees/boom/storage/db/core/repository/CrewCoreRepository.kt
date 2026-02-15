package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.crew.Crew
import io.lees.boom.core.domain.crew.CrewMember
import io.lees.boom.core.domain.crew.CrewMemberInfo
import io.lees.boom.core.domain.crew.CrewRankingInfo
import io.lees.boom.core.domain.crew.CrewRepository
import io.lees.boom.core.domain.crew.MyCrewInfo
import io.lees.boom.core.enums.CrewRole
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import io.lees.boom.storage.db.core.entity.CrewEntity
import io.lees.boom.storage.db.core.entity.CrewMemberEntity
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class CrewCoreRepository(
    private val crewJpaRepository: CrewJpaRepository,
    private val crewMemberJpaRepository: CrewMemberJpaRepository,
) : CrewRepository {
    override fun save(crew: Crew): Crew {
        val entity = crew.toEntity()
        return crewJpaRepository.save(entity).toDomain()
    }

    override fun saveMember(crewMember: CrewMember): CrewMember {
        val entity = crewMember.toEntity()
        return crewMemberJpaRepository.save(entity).toDomain()
    }

    @Transactional
    override fun saveCrewWithLeader(
        crew: Crew,
        leaderId: Long,
    ): Crew {
        val savedCrew = crewJpaRepository.save(crew.toEntity()).toDomain()
        val crewId = savedCrew.id ?: throw CoreException(CoreErrorType.CREW_CREATE_ERROR)

        val leader =
            CrewMemberEntity(
                crewId = crewId,
                memberId = leaderId,
                role = CrewRole.LEADER,
            )
        crewMemberJpaRepository.save(leader)

        return savedCrew
    }

    @Transactional
    override fun updateCrew(
        crewId: Long,
        maxMemberCount: Int?,
        crewImage: String?,
    ) {
        val entity = crewJpaRepository.findByIdOrNull(crewId) ?: return
        maxMemberCount?.let { entity.maxMemberCount = it }
        crewImage?.let { entity.crewImage = it }
    }

    override fun findCrewById(crewId: Long): Crew? {
        val entity = crewJpaRepository.findByIdOrNull(crewId) ?: return null
        val memberCount = crewMemberJpaRepository.countByCrewId(crewId).toInt()
        return entity.toDomain(memberCount)
    }

    override fun findCrewByIdForUpdate(crewId: Long): Crew? {
        val entity = crewJpaRepository.findByIdForUpdate(crewId) ?: return null
        val memberCount = crewMemberJpaRepository.countByCrewId(crewId).toInt()
        return entity.toDomain(memberCount)
    }

    override fun findCrewIdsByMemberId(memberId: Long): List<Long> =
        crewMemberJpaRepository.findByMemberId(memberId).map { it.crewId }

    override fun findMemberIdsByCrewIds(crewIds: List<Long>): Set<Long> =
        crewMemberJpaRepository.findByCrewIdIn(crewIds).map { it.memberId }.toSet()

    override fun findMyCrews(memberId: Long): List<MyCrewInfo> =
        crewMemberJpaRepository.findMyCrews(memberId).map { it.toDomain() }

    override fun findMembersWithInfoByCrewId(crewId: Long): List<CrewMemberInfo> =
        crewMemberJpaRepository.findMembersWithInfoByCrewId(crewId).map { it.toDomain() }

    override fun findMemberByCrewIdAndMemberId(
        crewId: Long,
        memberId: Long,
    ): CrewMember? = crewMemberJpaRepository.findByCrewIdAndMemberId(crewId, memberId)?.toDomain()

    @Transactional
    override fun softDeleteCrew(crewId: Long) {
        crewJpaRepository.softDelete(crewId)
    }

    @Transactional
    override fun softDeleteMember(crewMemberId: Long) {
        crewMemberJpaRepository.softDeleteById(crewMemberId)
    }

    @Transactional
    override fun softDeleteAllMembersByCrewId(crewId: Long) {
        crewMemberJpaRepository.softDeleteByCrewId(crewId)
    }

    override fun countLeadersByCrewId(crewId: Long): Long =
        crewMemberJpaRepository.countByCrewIdAndRole(crewId, CrewRole.LEADER)

    override fun findCrewsByLocation(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): List<Crew> {
        val latRange = radiusKm / 111.0
        // 경도 1도의 거리는 위도에 따라 달라짐 (한국 ~37° 기준 ≈ 88.8km)
        val lonRange = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)))

        val minLat = latitude - latRange
        val maxLat = latitude + latRange
        val minLon = longitude - lonRange
        val maxLon = longitude + lonRange

        val pageRequest = PageRequest.of(page, size)

        return crewJpaRepository
            .findByLocation(minLat, maxLat, minLon, maxLon, pageRequest)
            .content
            .map {
                val memberCount = crewMemberJpaRepository.countByCrewId(it.id).toInt()
                it.toDomain(memberCount)
            }
    }

    override fun findCrewRanking(
        page: Int,
        size: Int,
    ): List<Crew> {
        val pageRequest = PageRequest.of(page, size)
        return crewJpaRepository
            .findByOrderByActivityScoreDesc(pageRequest)
            .content
            .map {
                val memberCount = crewMemberJpaRepository.countByCrewId(it.id).toInt()
                it.toDomain(memberCount)
            }
    }

    override fun findCrewRankingByAvgScore(
        page: Int,
        size: Int,
    ): List<CrewRankingInfo> {
        val pageRequest = PageRequest.of(page, size)
        return crewJpaRepository
            .findCrewRankingByAvgScore(pageRequest)
            .content
            .map { it.toDomain() }
    }

    // Mapper Methods (Entity <-> Domain) 승준펀치 펀치입니다
    private fun Crew.toEntity() =
        CrewEntity(
            name = this.name,
            description = this.description,
            crewImage = this.crewImage,
            maxMemberCount = this.maxMemberCount,
            latitude = this.latitude,
            longitude = this.longitude,
            address = this.address,
            activityScore = this.activityScore,
        )

    private fun CrewEntity.toDomain(memberCount: Int = 0) =
        Crew(
            id = this.id,
            name = this.name,
            description = this.description,
            crewImage = this.crewImage,
            maxMemberCount = this.maxMemberCount,
            memberCount = memberCount,
            latitude = this.latitude,
            longitude = this.longitude,
            address = this.address,
            activityScore = this.activityScore,
        )

    private fun CrewMember.toEntity() =
        CrewMemberEntity(
            crewId = this.crewId,
            memberId = this.memberId,
            role = this.role,
        )

    private fun CrewMemberEntity.toDomain() =
        CrewMember(
            id = this.id,
            crewId = this.crewId,
            memberId = this.memberId,
            role = this.role,
        )

    private fun MyCrewProjection.toDomain() =
        MyCrewInfo(
            crewId = this.crewId,
            name = this.name,
            description = this.description,
            crewImage = this.crewImage,
            maxMemberCount = this.maxMemberCount,
            myRole = this.myRole,
            memberCount = this.memberCount,
            latitude = this.latitude,
            longitude = this.longitude,
            address = this.address,
        )

    private fun CrewMemberInfoProjection.toDomain() =
        CrewMemberInfo(
            memberId = this.memberId,
            name = this.memberName,
            profileImage = this.memberProfileImage,
            role = this.role,
        )

    private fun CrewRankingProjection.toDomain() =
        CrewRankingInfo(
            crewId = this.crewId,
            name = this.name,
            description = this.description,
            memberCount = this.memberCount,
            maxMemberCount = this.maxMemberCount,
            avgScore = this.avgScore,
            latitude = this.latitude,
            longitude = this.longitude,
            address = this.address,
        )
}
