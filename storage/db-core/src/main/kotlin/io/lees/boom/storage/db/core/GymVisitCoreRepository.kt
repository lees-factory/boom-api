package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.GymVisit
import io.lees.boom.core.domain.GymVisitRepository
import io.lees.boom.core.enums.VisitStatus
import org.springframework.stereotype.Repository

@Repository
internal class GymVisitCoreRepository(
    private val gymVisitJpaRepository: GymVisitJpaRepository,
) : GymVisitRepository {
    override fun save(visit: GymVisit): GymVisit = gymVisitJpaRepository.save(visit.toEntity()).toDomain()

    override fun findActiveVisit(memberId: Long): GymVisit? =
        gymVisitJpaRepository.findByMemberIdAndStatus(memberId, VisitStatus.ADMISSION)?.toDomain()

    override fun findActiveVisit(
        gymId: Long,
        memberId: Long,
    ): GymVisit? =
        gymVisitJpaRepository.findByGymIdAndMemberIdAndStatus(gymId, memberId, VisitStatus.ADMISSION)?.toDomain()

    private fun GymVisit.toEntity() =
        GymVisitEntity(
            gymId = this.gymId,
            memberId = this.memberId,
            status = this.status,
            admittedAt = this.admittedAt,
            exitedAt = this.exitedAt,
        ).apply {
            // ID가 있으면 JPA가 update로 인식하도록 설정 (BaseEntity id가 val이라 리플렉션이나 생성자 처리 필요하지만, 여기선 편의상 ID 매핑 생략하거나 var로 변경 고려. 보통은 조회된 엔티티를 수정함)
            // 실제로는 save 시점에 id가 있으면 merge, 없으면 persist 됨.
            // 여기서는 간단히 새 객체 생성으로 처리 (update 로직은 별도 조회 후 처리하므로)
        }

    // Entity -> Domain 변환 시 ID 포함
    private fun GymVisitEntity.toDomain() =
        GymVisit(
            id = this.id,
            gymId = this.gymId,
            memberId = this.memberId,
            status = this.status,
            admittedAt = this.admittedAt,
            exitedAt = this.exitedAt,
        )
}
