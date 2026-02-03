package io.lees.boom.core.domain

interface MemberTokenRepository {
    fun findByMemberId(memberId: Long): MemberToken?

    fun save(memberToken: MemberToken): MemberToken
}
