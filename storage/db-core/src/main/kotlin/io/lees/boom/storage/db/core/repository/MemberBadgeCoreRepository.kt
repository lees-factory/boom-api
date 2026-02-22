package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.badge.MemberBadge
import io.lees.boom.core.domain.badge.MemberBadgeRepository
import io.lees.boom.core.enums.BadgeType
import io.lees.boom.storage.db.core.entity.MemberBadgeEntity
import org.springframework.stereotype.Repository

@Repository
internal class MemberBadgeCoreRepository(
    private val memberBadgeJpaRepository: MemberBadgeJpaRepository,
) : MemberBadgeRepository {
    override fun findByMemberId(memberId: Long): List<MemberBadge> =
        memberBadgeJpaRepository.findByMemberId(memberId).map { it.toDomain() }

    override fun saveAll(badges: List<MemberBadge>): List<MemberBadge> =
        memberBadgeJpaRepository.saveAll(badges.map { it.toEntity() }).map { it.toDomain() }

    override fun deleteAllByMemberId(memberId: Long) {
        memberBadgeJpaRepository.deleteAllByMemberId(memberId)
    }

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
