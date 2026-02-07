package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.Crew
import io.lees.boom.core.domain.CrewMember
import io.lees.boom.core.domain.CrewMemberInfo
import io.lees.boom.core.domain.CrewRepository
import io.lees.boom.core.domain.MyCrewInfo
import io.lees.boom.core.enums.CrewRole
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
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
        crewJpaRepository.incrementMemberCount(crewId)

        return savedCrew
    }

    @Transactional
    override fun addMemberWithCount(crewMember: CrewMember): CrewMember {
        val saved = crewMemberJpaRepository.save(crewMember.toEntity()).toDomain()
        crewJpaRepository.incrementMemberCount(crewMember.crewId)
        return saved
    }

    override fun findCrewById(crewId: Long): Crew? = crewJpaRepository.findByIdOrNull(crewId)?.toDomain()

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

    override fun incrementMemberCount(crewId: Long) {
        crewJpaRepository.incrementMemberCount(crewId)
    }

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
            .map { it.toDomain() }
    }

    override fun findCrewRanking(
        page: Int,
        size: Int,
    ): List<Crew> {
        val pageRequest = PageRequest.of(page, size)
        return crewJpaRepository
            .findByOrderByActivityScoreDesc(pageRequest)
            .content
            .map { it.toDomain() }
    }

    // Mapper Methods (Entity <-> Domain) 승준펀치 펀치입니다
    private fun Crew.toEntity() =
        CrewEntity(
            name = this.name,
            description = this.description,
            maxMemberCount = this.maxMemberCount,
            memberCount = this.memberCount,
            latitude = this.latitude,
            longitude = this.longitude,
            address = this.address,
            activityScore = this.activityScore,
        )

    private fun CrewEntity.toDomain() =
        Crew(
            id = this.id,
            name = this.name,
            description = this.description,
            maxMemberCount = this.maxMemberCount,
            memberCount = this.memberCount,
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
            maxMemberCount = this.maxMemberCount,
            myRole = this.myRole,
            memberCount = this.memberCount,
        )

    private fun CrewMemberInfoProjection.toDomain() =
        CrewMemberInfo(
            memberId = this.memberId,
            name = this.memberName,
            profileImage = this.memberProfileImage,
            role = this.role,
        )
}
