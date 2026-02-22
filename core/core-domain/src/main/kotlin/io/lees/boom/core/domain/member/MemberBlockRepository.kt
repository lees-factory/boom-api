package io.lees.boom.core.domain.member

interface MemberBlockRepository {
    fun save(block: MemberBlock): MemberBlock

    fun findByBlockerIdAndBlockedId(
        blockerId: Long,
        blockedId: Long,
    ): MemberBlock?

    fun deleteByBlockerIdAndBlockedId(
        blockerId: Long,
        blockedId: Long,
    )

    fun findBlockedIdsByBlockerId(blockerId: Long): List<Long>

    fun deleteAllByMemberId(memberId: Long)
}
