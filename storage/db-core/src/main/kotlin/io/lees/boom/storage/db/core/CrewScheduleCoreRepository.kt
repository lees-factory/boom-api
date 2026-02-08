package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.CrewSchedule
import io.lees.boom.core.domain.CrewScheduleParticipant
import io.lees.boom.core.domain.CrewScheduleParticipantInfo
import io.lees.boom.core.domain.CrewScheduleRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
internal class CrewScheduleCoreRepository(
    private val crewScheduleJpaRepository: CrewScheduleJpaRepository,
    private val crewScheduleParticipantJpaRepository: CrewScheduleParticipantJpaRepository,
) : CrewScheduleRepository {
    override fun save(schedule: CrewSchedule): CrewSchedule =
        crewScheduleJpaRepository.save(schedule.toEntity()).toDomain()

    override fun findByCrewId(crewId: Long): List<CrewSchedule> =
        crewScheduleJpaRepository.findByCrewIdOrderByScheduledAtAsc(crewId).map { it.toDomain() }

    override fun findById(scheduleId: Long): CrewSchedule? =
        crewScheduleJpaRepository.findByIdOrNull(scheduleId)?.toDomain()

    override fun saveParticipant(participant: CrewScheduleParticipant): CrewScheduleParticipant =
        crewScheduleParticipantJpaRepository.save(participant.toEntity()).toDomain()

    override fun findParticipant(
        scheduleId: Long,
        memberId: Long,
    ): CrewScheduleParticipant? =
        crewScheduleParticipantJpaRepository.findByScheduleIdAndMemberId(scheduleId, memberId)?.toDomain()

    override fun findParticipantsByScheduleId(scheduleId: Long): List<CrewScheduleParticipantInfo> =
        crewScheduleParticipantJpaRepository.findParticipantsWithInfo(scheduleId).map { it.toDomain() }

    @Transactional
    override fun deleteParticipant(
        scheduleId: Long,
        memberId: Long,
    ) {
        crewScheduleParticipantJpaRepository.deleteByScheduleIdAndMemberId(scheduleId, memberId)
    }

    @Transactional
    override fun deleteSchedule(scheduleId: Long) {
        crewScheduleJpaRepository.deleteById(scheduleId)
    }

    @Transactional
    override fun deleteParticipantsByScheduleId(scheduleId: Long) {
        crewScheduleParticipantJpaRepository.deleteAllByScheduleId(scheduleId)
    }

    override fun findParticipantMemberIdsByScheduleId(scheduleId: Long): List<Long> =
        crewScheduleParticipantJpaRepository.findAllByScheduleId(scheduleId).map { it.memberId }

    // CrewSchedule Mappers
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

    // CrewScheduleParticipant Mappers
    private fun CrewScheduleParticipant.toEntity() =
        CrewScheduleParticipantEntity(
            scheduleId = this.scheduleId,
            memberId = this.memberId,
            participatedAt = this.participatedAt,
        )

    private fun CrewScheduleParticipantEntity.toDomain() =
        CrewScheduleParticipant(
            id = this.id,
            scheduleId = this.scheduleId,
            memberId = this.memberId,
            participatedAt = this.participatedAt,
        )

    private fun ScheduleParticipantInfoProjection.toDomain() =
        CrewScheduleParticipantInfo(
            memberId = this.memberId,
            name = this.name,
            profileImage = this.profileImage,
            participatedAt = this.participatedAt,
        )
}
