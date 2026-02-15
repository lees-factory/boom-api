package io.lees.boom.storage.db.core.repository

import io.lees.boom.core.domain.member.Member
import io.lees.boom.core.domain.member.MemberRepository
import io.lees.boom.core.domain.member.SocialInfo
import io.lees.boom.core.enums.SocialProvider
import io.lees.boom.storage.db.core.entity.MemberEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
internal class MemberCoreRepository(
    private val memberJpaRepository: MemberJpaRepository,
) : MemberRepository {
    override fun findBySocialInfo(
        provider: SocialProvider,
        socialId: String,
    ): Member? = memberJpaRepository.findByProviderAndSocialId(provider, socialId)?.toDomain()

    override fun findById(memberId: Long): Member? = memberJpaRepository.findById(memberId).orElse(null)?.toDomain()

    override fun save(member: Member): Member = memberJpaRepository.save(member.toEntity()).toDomain()

    override fun update(member: Member): Member {
        val entity =
            memberJpaRepository.findById(member.id!!).orElseThrow {
                IllegalStateException("Member not found: ${member.id}")
            }
        entity.name = member.name
        entity.email = member.email
        entity.profileImage = member.profileImage
        return entity.toDomain()
    }

    @Transactional
    override fun incrementActivityScore(
        memberId: Long,
        score: Int,
    ) {
        memberJpaRepository.incrementActivityScore(memberId, score)
    }

    @Transactional
    override fun decrementActivityScore(
        memberId: Long,
        score: Int,
    ) {
        memberJpaRepository.decrementActivityScore(memberId, score)
    }

    private fun Member.toEntity() =
        MemberEntity(
            name = this.name,
            email = this.email,
            profileImage = this.profileImage,
            role = this.role,
            provider = this.socialInfo.provider,
            socialId = this.socialInfo.socialId,
            activityScore = this.activityScore,
        )

    private fun MemberEntity.toDomain() =
        Member(
            id = this.id,
            name = this.name,
            email = this.email,
            profileImage = this.profileImage,
            role = this.role,
            socialInfo =
                SocialInfo(
                    provider = this.provider,
                    socialId = this.socialId,
                ),
            activityScore = this.activityScore,
        )
}
