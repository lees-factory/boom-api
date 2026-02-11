package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.domain.MemberBlockService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class BlockController(
    private val memberBlockService: MemberBlockService,
) {
    /**
     * 유저 차단
     * [POST] /api/v1/members/{targetMemberId}/block
     */
    @PostMapping("/{targetMemberId}/block")
    fun block(
        @User memberId: Long?,
        @PathVariable targetMemberId: Long,
    ): ApiResponse<Any> {
        memberBlockService.block(memberId!!, targetMemberId)
        return ApiResponse.success()
    }

    /**
     * 유저 차단 해제
     * [DELETE] /api/v1/members/{targetMemberId}/block
     */
    @DeleteMapping("/{targetMemberId}/block")
    fun unblock(
        @User memberId: Long?,
        @PathVariable targetMemberId: Long,
    ): ApiResponse<Any> {
        memberBlockService.unblock(memberId!!, targetMemberId)
        return ApiResponse.success()
    }
}
