package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.member.MemberBlock
import io.lees.boom.core.domain.member.MemberBlockRepository
import io.lees.boom.storage.db.core.entity.MemberBlockEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
internal class MemberBlockCoreRepository(
    private val memberBlockJpaRepository: MemberBlockJpaRepository,
) : MemberBlockRepository {
    override fun save(block: MemberBlock): MemberBlock = memberBlockJpaRepository.save(block.toEntity()).toDomain()

    override fun findByBlockerIdAndBlockedId(
        blockerId: Long,
        blockedId: Long,
    ): MemberBlock? = memberBlockJpaRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)?.toDomain()

    @Transactional
    override fun deleteByBlockerIdAndBlockedId(
        blockerId: Long,
        blockedId: Long,
    ) {
        memberBlockJpaRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId)
    }

    override fun findBlockedIdsByBlockerId(blockerId: Long): List<Long> =
        memberBlockJpaRepository.findAllByBlockerId(blockerId).map { it.blockedId }

    private fun MemberBlock.toEntity() =
        MemberBlockEntity(
            blockerId = this.blockerId,
            blockedId = this.blockedId,
        )

    private fun MemberBlockEntity.toDomain() =
        MemberBlock(
            id = this.id,
            blockerId = this.blockerId,
            blockedId = this.blockedId,
        )
}
