package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.GymVisit
import io.lees.boom.core.domain.GymVisitRepository
import io.lees.boom.core.enums.VisitStatus
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal class GymVisitCoreRepository(
    private val gymVisitJpaRepository: GymVisitJpaRepository,
) : GymVisitRepository {
    override fun save(visit: GymVisit): GymVisit = gymVisitJpaRepository.save(visit.toEntity()).toDomain()

    override fun findById(id: Long): GymVisit? = gymVisitJpaRepository.findByIdOrNull(id)?.toDomain()

    override fun findActiveVisit(memberId: Long): GymVisit? =
        gymVisitJpaRepository.findByMemberIdAndStatus(memberId, VisitStatus.ADMISSION)?.toDomain()

    override fun findActiveVisit(
        gymId: Long,
        memberId: Long,
    ): GymVisit? =
        gymVisitJpaRepository.findByGymIdAndMemberIdAndStatus(gymId, memberId, VisitStatus.ADMISSION)?.toDomain()

    override fun findStaleVisits(threshold: LocalDateTime): List<GymVisit> =
        gymVisitJpaRepository
            .findByStatusAndAdmittedAtBefore(VisitStatus.ADMISSION, threshold)
            .map { it.toDomain() }

    private fun GymVisit.toEntity() =
        GymVisitEntity(
            gymId = this.gymId,
            memberId = this.memberId,
            status = this.status,
            admittedAt = this.admittedAt,
            exitedAt = this.exitedAt,
            expiresAt = this.expiresAt,
        )

    private fun GymVisitEntity.toDomain() =
        GymVisit(
            id = this.id,
            gymId = this.gymId,
            memberId = this.memberId,
            status = this.status,
            admittedAt = this.admittedAt,
            exitedAt = this.exitedAt,
            expiresAt = this.expiresAt,
        )
}
