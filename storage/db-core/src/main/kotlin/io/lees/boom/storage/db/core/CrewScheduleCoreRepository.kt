package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.CrewSchedule
import io.lees.boom.core.domain.CrewScheduleRepository
import org.springframework.stereotype.Repository

@Repository
internal class CrewScheduleCoreRepository(
    private val crewScheduleJpaRepository: CrewScheduleJpaRepository,
) : CrewScheduleRepository {
    override fun save(schedule: CrewSchedule): CrewSchedule =
        crewScheduleJpaRepository.save(schedule.toEntity()).toDomain()

    override fun findByCrewId(crewId: Long): List<CrewSchedule> =
        crewScheduleJpaRepository.findByCrewIdOrderByScheduledAtAsc(crewId).map { it.toDomain() }

    private fun CrewSchedule.toEntity() =
        CrewScheduleEntity(
            crewId = this.crewId,
            gymId = this.gymId,
            title = this.title,
            description = this.description,
            scheduledAt = this.scheduledAt,
            createdBy = this.createdBy,
        )

    private fun CrewScheduleEntity.toDomain() =
        CrewSchedule(
            id = this.id,
            crewId = this.crewId,
            gymId = this.gymId,
            title = this.title,
            description = this.description,
            scheduledAt = this.scheduledAt,
            createdBy = this.createdBy,
        )
}
