package io.lees.boom.storage.db.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "member_token")
class MemberTokenEntity(
    @Column(nullable = false, unique = true)
    val memberId: Long,
    @Column(nullable = false)
    var refreshToken: String,
    @Column(nullable = false)
    var expirationDateTime: LocalDateTime,
) : BaseEntity() {
    fun update(
        refreshToken: String,
        expirationDateTime: LocalDateTime,
    ) {
        this.refreshToken = refreshToken
        this.expirationDateTime = expirationDateTime
    }
}
