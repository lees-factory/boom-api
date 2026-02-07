package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.Member
import io.lees.boom.core.domain.MemberRepository
import io.lees.boom.core.domain.SocialInfo
import io.lees.boom.core.enums.SocialProvider
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

    private fun Member.toEntity() =
        MemberEntity(
            name = this.name,
            email = this.email,
            profileImage = this.profileImage, // [추가]
            role = this.role,
            provider = this.socialInfo.provider,
            socialId = this.socialInfo.socialId,
        )

    private fun MemberEntity.toDomain() =
        Member(
            id = this.id,
            name = this.name,
            email = this.email,
            profileImage = this.profileImage, // [추가]
            role = this.role,
            socialInfo =
                SocialInfo(
                    provider = this.provider,
                    socialId = this.socialId,
                ),
        )
}
