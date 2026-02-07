package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.MemberBadge
import io.lees.boom.core.domain.MemberBadgeRepository
import io.lees.boom.core.enums.BadgeType
import org.springframework.stereotype.Repository

@Repository
internal class MemberBadgeCoreRepository(
    private val memberBadgeJpaRepository: MemberBadgeJpaRepository,
) : MemberBadgeRepository {
    override fun findByMemberId(memberId: Long): List<MemberBadge> =
        memberBadgeJpaRepository.findByMemberId(memberId).map { it.toDomain() }

    override fun saveAll(badges: List<MemberBadge>): List<MemberBadge> =
        memberBadgeJpaRepository.saveAll(badges.map { it.toEntity() }).map { it.toDomain() }

    private fun MemberBadge.toEntity() =
        MemberBadgeEntity(
            memberId = this.memberId,
            badgeType = this.badgeType.name,
            acquiredAt = this.acquiredAt,
            notified = this.notified,
        )

    private fun MemberBadgeEntity.toDomain() =
        MemberBadge(
            id = this.id,
            memberId = this.memberId,
            badgeType = BadgeType.valueOf(this.badgeType),
            acquiredAt = this.acquiredAt,
            notified = this.notified,
        )
}
