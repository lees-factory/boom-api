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

    override fun save(member: Member): Member = memberJpaRepository.save(member.toEntity()).toDomain()

    // [Mapper] Domain -> Entity
    private fun Member.toEntity() =
        MemberEntity(
            name = this.name,
            email = this.email,
            role = this.role, // [추가] 매핑
            provider = this.socialInfo.provider,
            socialId = this.socialInfo.socialId,
        )

    // [Mapper] Entity -> Domain
    private fun MemberEntity.toDomain() =
        Member(
            id = this.id,
            name = this.name,
            email = this.email,
            role = this.role, // [추가] 매핑
            socialInfo =
                SocialInfo(
                    provider = this.provider,
                    socialId = this.socialId,
                ),
        )
}
