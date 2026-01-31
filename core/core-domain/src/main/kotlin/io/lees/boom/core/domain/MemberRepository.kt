package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider

interface MemberRepository {
    suspend fun save(member: Member): Member

    suspend fun findBySocialInfo(
        provider: SocialProvider,
        socialId: String,
    ): Member?
}
