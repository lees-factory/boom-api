package io.lees.boom.storage.db.core

import io.lees.boom.core.domain.MemberNotificationSetting
import io.lees.boom.core.domain.MemberNotificationSettingRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class MemberNotificationSettingCoreRepository(
    private val memberNotificationSettingJpaRepository: MemberNotificationSettingJpaRepository,
) : MemberNotificationSettingRepository {
    override fun findByMemberId(memberId: Long): MemberNotificationSetting? =
        memberNotificationSettingJpaRepository.findByMemberId(memberId)?.toDomain()

    override fun save(setting: MemberNotificationSetting): MemberNotificationSetting =
        memberNotificationSettingJpaRepository.save(setting.toEntity()).toDomain()

    @Transactional
    override fun update(setting: MemberNotificationSetting) {
        val entity = memberNotificationSettingJpaRepository.findByMemberId(setting.memberId) ?: return
        entity.pushToken = setting.pushToken
        entity.pushEnabled = setting.pushEnabled
        entity.crewScheduleEnabled = setting.crewScheduleEnabled
        entity.crewChatEnabled = setting.crewChatEnabled
    }

    private fun MemberNotificationSetting.toEntity() =
        MemberNotificationSettingEntity(
            memberId = this.memberId,
            pushToken = this.pushToken,
            pushEnabled = this.pushEnabled,
            crewScheduleEnabled = this.crewScheduleEnabled,
            crewChatEnabled = this.crewChatEnabled,
        )

    private fun MemberNotificationSettingEntity.toDomain() =
        MemberNotificationSetting(
            id = this.id,
            memberId = this.memberId,
            pushToken = this.pushToken,
            pushEnabled = this.pushEnabled,
            crewScheduleEnabled = this.crewScheduleEnabled,
            crewChatEnabled = this.crewChatEnabled,
        )
}
