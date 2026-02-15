package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.auth.MemberToken
import io.lees.boom.core.domain.auth.MemberTokenRepository
import io.lees.boom.storage.db.core.entity.MemberTokenEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class MemberTokenCoreRepository(
    private val memberTokenJpaRepository: MemberTokenJpaRepository,
) : MemberTokenRepository {
    @Transactional
    override fun store(memberToken: MemberToken): MemberToken {
        val entity =
            memberTokenJpaRepository.findByMemberId(memberToken.memberId)
                ?: MemberTokenEntity(
                    memberId = memberToken.memberId,
                    refreshToken = memberToken.refreshToken,
                    expirationDateTime = memberToken.expirationDateTime,
                )

        entity.update(memberToken.refreshToken, memberToken.expirationDateTime)
        return memberTokenJpaRepository.save(entity).toDomain()
    }

    // [추가] 인터페이스 구현
    @Transactional(readOnly = true)
    override fun findByRefreshToken(refreshToken: String): MemberToken? =
        memberTokenJpaRepository.findByRefreshToken(refreshToken)?.toDomain()

    @Transactional(readOnly = true)
    override fun findByMemberId(memberId: Long): MemberToken? =
        memberTokenJpaRepository.findByMemberId(memberId)?.toDomain()

    private fun MemberTokenEntity.toDomain() =
        MemberToken(
            memberId = this.memberId,
            refreshToken = this.refreshToken,
            expirationDateTime = this.expirationDateTime,
        )
}
