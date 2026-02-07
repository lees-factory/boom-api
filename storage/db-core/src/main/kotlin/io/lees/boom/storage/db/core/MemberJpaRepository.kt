package io.lees.boom.storage.db.core

import io.lees.boom.core.enums.SocialProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByProviderAndSocialId(
        provider: SocialProvider,
        socialId: String,
    ): MemberEntity?

    @Modifying
    @Query("UPDATE MemberEntity m SET m.activityScore = m.activityScore + :score WHERE m.id = :memberId")
    fun incrementActivityScore(
        @Param("memberId") memberId: Long,
        @Param("score") score: Int,
    )
}
