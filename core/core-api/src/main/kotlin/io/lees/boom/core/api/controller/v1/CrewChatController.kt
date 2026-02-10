package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.ChatMessageCreateRequest
import io.lees.boom.core.api.controller.v1.response.ChatMessageIdResponse
import io.lees.boom.core.api.controller.v1.response.ChatMessageResponse
import io.lees.boom.core.domain.CrewChatService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/crews/{crewId}/chat")
class CrewChatController(
    private val crewChatService: CrewChatService,
) {
    /**
     * 메시지 전송
     * [POST] /api/v1/crews/{crewId}/chat
     */
    @PostMapping
    fun sendMessage(
        @User memberId: Long?,
        @PathVariable crewId: Long,
        @RequestBody request: ChatMessageCreateRequest,
    ): ApiResponse<ChatMessageIdResponse> {
        val message = crewChatService.sendMessage(crewId, memberId!!, request.content)
        return ApiResponse.success(ChatMessageIdResponse(message.id!!))
    }

    /**
     * 메시지 이력 조회 (커서 페이지네이션)
     * [GET] /api/v1/crews/{crewId}/chat?cursor={id}&size=10
     */
    @GetMapping
    fun getMessages(
        @User memberId: Long?,
        @PathVariable crewId: Long,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "10") size: Int,
    ): ApiResponse<List<ChatMessageResponse>> {
        val messages = crewChatService.getMessages(crewId, memberId!!, cursor, size)
        return ApiResponse.success(messages.map { ChatMessageResponse.from(it) })
    }

    /**
     * 메시지 삭제 (작성자 또는 LEADER)
     * [DELETE] /api/v1/crews/{crewId}/chat/{messageId}
     */
    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @User memberId: Long?,
        @PathVariable crewId: Long,
        @PathVariable messageId: Long,
    ): ApiResponse<Any> {
        crewChatService.deleteMessage(crewId, messageId, memberId!!)
        return ApiResponse.success()
    }
}
