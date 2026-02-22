package io.lees.boom.core.domain.member

import io.lees.boom.core.enums.SocialProvider

interface MemberRepository {
    fun save(member: Member): Member

    fun findBySocialInfo(
        provider: SocialProvider,
        socialId: String,
    ): Member?

    fun findById(memberId: Long): Member?

    fun update(member: Member): Member

    fun incrementActivityScore(
        memberId: Long,
        score: Int,
    )

    fun decrementActivityScore(
        memberId: Long,
        score: Int,
    )

    fun deleteById(memberId: Long)
}
