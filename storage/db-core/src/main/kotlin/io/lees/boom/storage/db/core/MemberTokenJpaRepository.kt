package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface MemberTokenJpaRepository : JpaRepository<MemberTokenEntity, Long> {
    fun findByMemberId(memberId: Long): MemberTokenEntity?
}
