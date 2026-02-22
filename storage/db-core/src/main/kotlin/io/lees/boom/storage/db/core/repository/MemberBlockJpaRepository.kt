package io.lees.boom.storage.db.core.repository

import io.lees.boom.storage.db.core.entity.MemberBlockEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberBlockJpaRepository : JpaRepository<MemberBlockEntity, Long> {
    fun findByBlockerIdAndBlockedId(
        blockerId: Long,
        blockedId: Long,
    ): MemberBlockEntity?

    fun deleteByBlockerIdAndBlockedId(
        blockerId: Long,
        blockedId: Long,
    )

    fun findAllByBlockerId(blockerId: Long): List<MemberBlockEntity>

    fun deleteAllByBlockerId(blockerId: Long)

    fun deleteAllByBlockedId(blockedId: Long)
}
