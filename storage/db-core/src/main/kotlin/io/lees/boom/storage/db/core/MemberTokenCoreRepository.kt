package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.MemberToken
import io.lees.boom.core.domain.MemberTokenRepository
import org.springframework.stereotype.Repository

@Repository
internal class MemberTokenCoreRepository(
    private val memberTokenJpaRepository: MemberTokenJpaRepository,
) : MemberTokenRepository {
    override fun findByMemberId(memberId: Long): MemberToken? =
        memberTokenJpaRepository.findByMemberId(memberId)?.toDomain()

    override fun save(memberToken: MemberToken): MemberToken {
        val existingEntity = memberTokenJpaRepository.findByMemberId(memberToken.memberId)

        val entityToSave =
            existingEntity
                ?.apply {
                    update(memberToken.refreshToken, memberToken.expirationDateTime)
                }
                ?: MemberTokenEntity(
                    memberId = memberToken.memberId,
                    refreshToken = memberToken.refreshToken,
                    expirationDateTime = memberToken.expirationDateTime,
                )

        return memberTokenJpaRepository.save(entityToSave).toDomain()
    }

    private fun MemberTokenEntity.toDomain() =
        MemberToken(
            memberId = this.memberId,
            refreshToken = this.refreshToken,
            expirationDateTime = this.expirationDateTime,
        )
}
