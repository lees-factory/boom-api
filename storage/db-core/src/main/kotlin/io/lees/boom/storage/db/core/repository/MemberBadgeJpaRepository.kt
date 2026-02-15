package io.lees.boom.storage.db.core.repository

import io.lees.boom.storage.db.core.entity.MemberBadgeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberBadgeJpaRepository : JpaRepository<MemberBadgeEntity, Long> {
    fun findByMemberId(memberId: Long): List<MemberBadgeEntity>
}
