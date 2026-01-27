package io.lees.boom.storage.db.core

import io.lees.boom.core.enums.SocialProvider
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByProviderAndSocialId(provider: SocialProvider, socialId: String): MemberEntity?
}
