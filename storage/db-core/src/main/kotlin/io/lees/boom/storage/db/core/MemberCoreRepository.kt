package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.Member
import io.lees.boom.core.domain.MemberRepository
import io.lees.boom.core.domain.SocialInfo
import io.lees.boom.core.enums.SocialProvider
import org.springframework.stereotype.Repository

@Repository
internal class MemberCoreRepository (
    private  val memberJpaRepository: MemberJpaRepository
): MemberRepository {
    override suspend fun save(member: Member): Member {
        val entity = member.toEntity()
        val savedEntity = memberJpaRepository.save(entity)
        return savedEntity.toDomain()
    }


    override suspend fun findBySocialInfo(provider: SocialProvider, socialId: String): Member? {
        val entity = memberJpaRepository.findByProviderAndSocialId(provider, socialId)
        return entity?.toDomain()
    }



    private fun Member.toEntity(): MemberEntity {
        return MemberEntity(
            name = this.name,
            email = this.email,
            provider = this.socialInfo.provider,
            socialId = this.socialInfo.socialId
        )
        // ID가 있다면 update 로직 등이 추가될 수 있음 (JPA의 merge 동작 활용)
    }

    private fun MemberEntity.toDomain(): Member {
        return Member(
            id = this.id,
            name = this.name,
            email = this.email,
            socialInfo = SocialInfo(
                provider = this.provider,
                socialId = this.socialId
            )
        )
    }
}