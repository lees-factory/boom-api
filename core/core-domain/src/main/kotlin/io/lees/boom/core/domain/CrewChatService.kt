package io.lees.boom.core.domain

import io.lees.boom.core.enums.CrewRole
import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CrewChatService(
    private val crewChatMessageReader: CrewChatMessageReader,
    private val crewChatMessageAppender: CrewChatMessageAppender,
    private val crewMemberReader: CrewMemberReader,
) {
    companion object {
        private const val RATE_LIMIT_COUNT = 5
        private const val RATE_LIMIT_SECONDS = 1L
    }

    /**
     * 메시지 전송
     * 1. 크루 멤버 검증
     * 2. 속도 제한 (1초에 5건)
     * 3. 저장
     */
    fun sendMessage(
        crewId: Long,
        memberId: Long,
        content: String,
    ): CrewChatMessage {
        crewMemberReader.readCrewMember(crewId, memberId)
            ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        val since = LocalDateTime.now().minusSeconds(RATE_LIMIT_SECONDS)
        val recentCount = crewChatMessageReader.countRecentMessages(crewId, memberId, since)
        if (recentCount >= RATE_LIMIT_COUNT) {
            throw CoreException(CoreErrorType.CHAT_RATE_LIMIT_EXCEEDED)
        }

        val message = CrewChatMessage.create(crewId, memberId, content)
        return crewChatMessageAppender.append(message)
    }

    /**
     * 메시지 이력 조회 (커서 페이지네이션)
     * 크루 멤버만 조회 가능
     */
    fun getMessages(
        crewId: Long,
        memberId: Long,
        cursor: Long?,
        size: Int,
    ): List<CrewChatMessageInfo> {
        crewMemberReader.readCrewMember(crewId, memberId)
            ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        return crewChatMessageReader.readMessages(crewId, cursor, size)
    }

    /**
     * 메시지 삭제 (작성자 또는 LEADER만 가능)
     */
    fun deleteMessage(
        crewId: Long,
        messageId: Long,
        memberId: Long,
    ) {
        val crewMember =
            crewMemberReader.readCrewMember(crewId, memberId)
                ?: throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)

        val message =
            crewChatMessageReader.readById(messageId)
                ?: throw CoreException(CoreErrorType.CHAT_MESSAGE_NOT_FOUND)

        if (message.crewId != crewId) {
            throw CoreException(CoreErrorType.CHAT_MESSAGE_NOT_FOUND)
        }

        if (message.memberId != memberId && crewMember.role != CrewRole.LEADER) {
            throw CoreException(CoreErrorType.CREW_MEMBER_NOT_AUTHORIZED)
        }

        crewChatMessageAppender.softDelete(messageId)
    }
}
