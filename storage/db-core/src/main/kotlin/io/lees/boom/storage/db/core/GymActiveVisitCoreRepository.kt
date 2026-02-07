package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.GymActiveVisit
import io.lees.boom.core.domain.GymActiveVisitRepository
import io.lees.boom.core.domain.GymCrewMemberInfo
import io.lees.boom.core.domain.GymVisitor
import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.springframework.data.domain.PageRequest as JpaPageRequest

@Repository
internal class GymActiveVisitCoreRepository(
    private val gymActiveVisitJpaRepository: GymActiveVisitJpaRepository,
) : GymActiveVisitRepository {
    @Transactional
    override fun save(activeVisit: GymActiveVisit): GymActiveVisit {
        val existingEntity = gymActiveVisitJpaRepository.findByMemberId(activeVisit.memberId)

        val entity =
            if (existingEntity != null) {
                // 기존 엔티티가 있으면 업데이트 (다른 암장으로 이동하는 경우 등)
                // 이 경우는 사실 발생하면 안 되지만, 안전하게 처리
                existingEntity.expiresAt = activeVisit.expiresAt
                existingEntity
            } else {
                // 새로 생성
                activeVisit.toEntity()
            }

        return gymActiveVisitJpaRepository.save(entity).toDomain()
    }

    @Transactional
    override fun delete(memberId: Long) {
        gymActiveVisitJpaRepository.deleteByMemberId(memberId)
    }

    override fun findByMemberId(memberId: Long): GymActiveVisit? =
        gymActiveVisitJpaRepository.findByMemberId(memberId)?.toDomain()

    override fun findByGymIdAndMemberId(
        gymId: Long,
        memberId: Long,
    ): GymActiveVisit? = gymActiveVisitJpaRepository.findByGymIdAndMemberId(gymId, memberId)?.toDomain()

    override fun existsByMemberId(memberId: Long): Boolean = gymActiveVisitJpaRepository.existsByMemberId(memberId)

    override fun findExpiredVisits(now: LocalDateTime): List<GymActiveVisit> =
        gymActiveVisitJpaRepository.findByExpiresAtBefore(now).map { it.toDomain() }

    override fun findVisitorsByGymId(
        gymId: Long,
        pageRequest: PageRequest,
    ): SliceResult<GymVisitor> {
        val pageable = JpaPageRequest.of(pageRequest.page, pageRequest.size + 1)
        val projections = gymActiveVisitJpaRepository.findVisitorsByGymId(gymId, pageable)

        val visitors =
            projections.map { projection ->
                GymVisitor(
                    memberId = projection.memberId,
                    memberName = projection.memberName,
                    memberProfileImage = projection.memberProfileImage,
                    admittedAt = projection.admittedAt,
                )
            }

        return SliceResult.fromLimitPlusOne(visitors, pageRequest)
    }

    override fun findCrewMemberVisits(
        memberIds: Set<Long>,
        gymIds: Set<Long>,
    ): Map<Long, List<GymCrewMemberInfo>> {
        val projections = gymActiveVisitJpaRepository.findCrewMemberVisits(memberIds, gymIds)
        return projections
            .groupBy { it.gymId }
            .mapValues { (_, visitors) ->
                visitors.map { projection ->
                    GymCrewMemberInfo(
                        memberId = projection.memberId,
                        memberName = projection.memberName,
                        memberProfileImage = projection.memberProfileImage,
                    )
                }
            }
    }

    private fun GymActiveVisit.toEntity() =
        GymActiveVisitEntity(
            gymId = this.gymId,
            memberId = this.memberId,
            admittedAt = this.admittedAt,
            expiresAt = this.expiresAt,
        )

    private fun GymActiveVisitEntity.toDomain() =
        GymActiveVisit(
            id = this.id,
            gymId = this.gymId,
            memberId = this.memberId,
            admittedAt = this.admittedAt,
            expiresAt = this.expiresAt,
        )
}
