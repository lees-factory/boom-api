package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.Crew
import io.lees.boom.core.domain.CrewMember
import io.lees.boom.core.domain.CrewMemberInfo
import io.lees.boom.core.domain.CrewRepository
import io.lees.boom.core.domain.MyCrewInfo
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

    // Mapper Methods (Entity <-> Domain) 승준펀치 펀치입니다
    private fun Crew.toEntity() =
        CrewEntity(
            name = this.name,
            description = this.description,
            maxMemberCount = this.maxMemberCount,
        )

    private fun CrewEntity.toDomain() =
        Crew(
            id = this.id,
            name = this.name,
            description = this.description,
            maxMemberCount = this.maxMemberCount,
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
