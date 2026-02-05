package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider

interface MemberRepository {
    fun save(member: Member): Member

    fun findBySocialInfo(
        provider: SocialProvider,
        socialId: String,
    ): Member?

    fun findById(memberId: Long): Member?
}
