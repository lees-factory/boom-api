package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.MemberToken
import io.lees.boom.core.domain.MemberTokenRepository
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
    override fun findByRefreshToken(refreshToken: String): MemberToken? {
        return memberTokenJpaRepository.findByRefreshToken(refreshToken)?.toDomain()
    }

    @Transactional(readOnly = true)
    override fun findByMemberId(memberId: Long): MemberToken? {
        return memberTokenJpaRepository.findByMemberId(memberId)?.toDomain()
    }

    private fun MemberTokenEntity.toDomain() =
        MemberToken(
            memberId = this.memberId,
            refreshToken = this.refreshToken,
            expirationDateTime = this.expirationDateTime,
        )
}
