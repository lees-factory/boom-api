package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.GymVisit
import io.lees.boom.core.domain.GymVisitRepository
import io.lees.boom.core.domain.GymVisitor
import io.lees.boom.core.enums.VisitStatus
import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import org.springframework.data.domain.PageRequest as JpaPageRequest

@Repository
internal class GymVisitCoreRepository(
    private val gymVisitJpaRepository: GymVisitJpaRepository,
) : GymVisitRepository {
    override fun save(visit: GymVisit): GymVisit {
        val visitId = visit.id
        val entity =
            if (visitId != null) {
                // 기존 엔티티 업데이트
                val existing = gymVisitJpaRepository.findById(visitId).orElseThrow()
                existing.status = visit.status
                existing.exitedAt = visit.exitedAt
                existing.expiresAt = visit.expiresAt
                existing
            } else {
                // 새 엔티티 생성
                visit.toEntity()
            }
        return gymVisitJpaRepository.save(entity).toDomain()
    }

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

    override fun findActiveVisitorsByGymId(
        gymId: Long,
        pageRequest: PageRequest,
    ): SliceResult<GymVisitor> {
        val pageable = JpaPageRequest.of(pageRequest.page, pageRequest.size + 1)
        val projections = gymVisitJpaRepository.findActiveVisitorsByGymId(gymId, pageable)

        val visitors =
            projections.map { projection ->
                GymVisitor(
                    memberId = projection.getMemberId(),
                    memberName = projection.getMemberName(),
                    memberProfileImage = projection.getMemberProfileImage(),
                    admittedAt = projection.getAdmittedAt(),
                )
            }

        return SliceResult.fromLimitPlusOne(visitors, pageRequest)
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
