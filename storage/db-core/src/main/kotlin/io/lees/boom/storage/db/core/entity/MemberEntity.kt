package io.lees.boom.storage.db.core.entity

import io.lees.boom.core.enums.MemberRole
import io.lees.boom.core.enums.SocialProvider
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class MemberEntity(
    var name: String,
    var email: String?,
    @Column(name = "profile_image")
    var profileImage: String?,
    val socialId: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: MemberRole,
    @Enumerated(EnumType.STRING)
    val provider: SocialProvider,
    var activityScore: Int = 0,
) : BaseEntity()
