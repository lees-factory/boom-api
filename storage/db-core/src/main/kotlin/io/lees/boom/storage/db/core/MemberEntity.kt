package io.lees.boom.storage.db.core

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

    val email: String,

    val socialId: String,

    @Enumerated(EnumType.STRING)
    val provider: SocialProvider,


    ) : BaseEntity()
