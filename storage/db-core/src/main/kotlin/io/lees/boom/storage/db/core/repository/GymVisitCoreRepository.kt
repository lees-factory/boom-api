package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.gym.GymVisit
import io.lees.boom.core.domain.gym.GymVisitRepository
import io.lees.boom.storage.db.core.entity.GymVisitEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

/**
 * 방문 히스토리 저장소 (통계용)
 * - 항상 INSERT (UPDATE 없음)
 * - 모든 방문 기록 보존
 */
@Repository
internal class GymVisitCoreRepository(
    private val gymVisitJpaRepository: GymVisitJpaRepository,
) : GymVisitRepository {
    /**
     * 방문 기록 저장 (항상 INSERT)
     */
    override fun save(visit: GymVisit): GymVisit = gymVisitJpaRepository.save(visit.toEntity()).toDomain()

    override fun findById(id: Long): GymVisit? = gymVisitJpaRepository.findByIdOrNull(id)?.toDomain()

    override fun deleteAllByMemberId(memberId: Long) {
        gymVisitJpaRepository.deleteAllByMemberId(memberId)
    }

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
