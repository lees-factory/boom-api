package io.lees.boom.storage.db.core

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
    val name: String,
    val email: String?,
    @Column(name = "profile_image")
    val profileImage: String?, // [추가]
    val socialId: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: MemberRole,
    @Enumerated(EnumType.STRING)
    val provider: SocialProvider,
) : BaseEntity()
