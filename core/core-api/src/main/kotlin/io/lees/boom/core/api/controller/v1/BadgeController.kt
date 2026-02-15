package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.response.BadgeResponse
import io.lees.boom.core.domain.badge.BadgeService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class BadgeController(
    private val badgeService: BadgeService,
) {
    /**
     * 내 뱃지 조회 (on-demand 계산 + 저장)
     * [GET] /api/v1/members/me/badges
     */
    @GetMapping("/me/badges")
    fun getMyBadges(
        @User memberId: Long,
    ): ApiResponse<List<BadgeResponse>> {
        val badges = badgeService.getMyBadges(memberId)
        return ApiResponse.success(badges.map { BadgeResponse.from(it) })
    }

    /**
     * 타 유저 뱃지 조회 (저장된 것만 조회)
     * [GET] /api/v1/members/{memberId}/badges
     */
    @GetMapping("/{memberId}/badges")
    fun getMemberBadges(
        @PathVariable memberId: Long,
    ): ApiResponse<List<BadgeResponse>> {
        val badges = badgeService.getMemberBadges(memberId)
        return ApiResponse.success(badges.map { BadgeResponse.from(it) })
    }
}
