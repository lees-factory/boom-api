package io.lees.boom.core.domain.member

import io.lees.boom.core.domain.auth.RefreshTokenStore
import io.lees.boom.core.domain.badge.MemberBadgeWriter
import io.lees.boom.core.domain.crew.CrewAppender
import io.lees.boom.core.domain.crew.CrewChatMessageAppender
import io.lees.boom.core.domain.crew.CrewScheduleAppender
import io.lees.boom.core.domain.gym.GymActiveVisitWriter
import io.lees.boom.core.domain.gym.GymVisitAppender
import io.lees.boom.core.domain.report.ReportWriter
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MemberWithdrawService(
    private val memberFinder: MemberFinder,
    private val memberDeleter: MemberDeleter,
    private val refreshTokenStore: RefreshTokenStore,
    private val gymActiveVisitWriter: GymActiveVisitWriter,
    private val gymVisitAppender: GymVisitAppender,
    private val memberBadgeWriter: MemberBadgeWriter,
    private val memberNotificationSettingWriter: MemberNotificationSettingWriter,
    private val memberBlockWriter: MemberBlockWriter,
    private val crewAppender: CrewAppender,
    private val crewChatMessageAppender: CrewChatMessageAppender,
    private val crewScheduleAppender: CrewScheduleAppender,
    private val reportWriter: ReportWriter,
) {
    @Transactional
    fun withdraw(memberId: Long) {
        memberFinder.findById(memberId)
            ?: throw CoreException(CoreErrorType.NOT_FOUND_MEMBER)

        // 관련 데이터 삭제 (각 개념의 도구클래스에 위임)
        refreshTokenStore.deleteByMemberId(memberId)
        gymActiveVisitWriter.delete(memberId)
        gymVisitAppender.deleteAllByMemberId(memberId)
        memberBadgeWriter.deleteAllByMemberId(memberId)
        memberNotificationSettingWriter.deleteByMemberId(memberId)
        memberBlockWriter.deleteAllByMemberId(memberId)
        crewScheduleAppender.removeAllParticipantsByMemberId(memberId)
        crewAppender.softDeleteMembersByMemberId(memberId)
        crewChatMessageAppender.softDeleteByMemberId(memberId)
        reportWriter.deleteAllByReporterId(memberId)

        // 회원 삭제
        memberDeleter.delete(memberId)
    }
}
