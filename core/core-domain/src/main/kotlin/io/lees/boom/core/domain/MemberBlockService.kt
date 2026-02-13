package io.lees.boom.core.domain

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Service

@Service
class MemberBlockService(
    private val memberBlockRepository: MemberBlockRepository,
) {
    fun block(
        blockerId: Long,
        blockedId: Long,
    ) {
        memberBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)?.let {
            throw CoreException(CoreErrorType.ALREADY_BLOCKED)
        }

        val block = MemberBlock.create(blockerId, blockedId)
        memberBlockRepository.save(block)
    }

    fun unblock(
        blockerId: Long,
        blockedId: Long,
    ) {
        memberBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
            ?: throw CoreException(CoreErrorType.NOT_BLOCKED)

        memberBlockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId)
    }

    fun getBlockedMemberIds(blockerId: Long): List<Long> = memberBlockRepository.findBlockedIdsByBlockerId(blockerId)

    fun isBlocked(
        blockerId: Long,
        blockedId: Long,
    ): Boolean = memberBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId) != null
}
