package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface MemberBadgeJpaRepository : JpaRepository<MemberBadgeEntity, Long> {
    fun findByMemberId(memberId: Long): List<MemberBadgeEntity>
}
