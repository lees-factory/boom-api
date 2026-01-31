package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.Crew
import io.lees.boom.core.domain.CrewMember
import io.lees.boom.core.domain.CrewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class CrewCoreRepository(
    private val crewJpaRepository: CrewJpaRepository,
    private val crewMemberJpaRepository: CrewMemberJpaRepository,
) : CrewRepository {
    override suspend fun save(crew: Crew): Crew {
        val entity = crew.toEntity()
        return crewJpaRepository.save(entity).toDomain()
    }

    override suspend fun saveMember(crewMember: CrewMember): CrewMember {
        val entity = crewMember.toEntity()
        return crewMemberJpaRepository.save(entity).toDomain()
    }

    override suspend fun findCrewById(crewId: Long): Crew? = crewJpaRepository.findByIdOrNull(crewId)?.toDomain()

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
}
