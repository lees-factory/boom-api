package io.lees.boom.core.domain.badge

interface MemberBadgeRepository {
    fun findByMemberId(memberId: Long): List<MemberBadge>

    fun saveAll(badges: List<MemberBadge>): List<MemberBadge>
}
