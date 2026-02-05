package io.lees.boom.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface MemberTokenJpaRepository : JpaRepository<MemberTokenEntity, Long> {
    fun findByMemberId(memberId: Long): MemberTokenEntity?

    // [추가] JPA가 자동으로 쿼리 생성 (select * from member_token where refresh_token = ?)
    fun findByRefreshToken(refreshToken: String): MemberTokenEntity?
}
