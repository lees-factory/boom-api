package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.CrewCreateRequest
import io.lees.boom.core.api.controller.v1.response.CrewIdResponse
import io.lees.boom.core.domain.CrewService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/crews")
class CrewController(
    private val crewService: CrewService,
) {
    /**
     * 크루 생성
     * [POST] /api/v1/crews
     * @User memberId: Resolver가 인증 후 주입해주는 ID
     */
    @PostMapping
    fun createCrew(
        @User memberId: Long?, // Header 직접 접근 제거 -> Resolver 사용
        @RequestBody request: CrewCreateRequest,
    ): ApiResponse<CrewIdResponse> {
        val createdCrew =
            crewService.createCrew(
                memberId = memberId!!,
                name = request.name,
                description = request.description,
                maxMemberCount = request.maxMemberCount ?: 100, // [추가]
            )

        // ID는 Service에서 보장됨
        return ApiResponse.success(CrewIdResponse(createdCrew.id!!))
    }

    /**
     * 크루 가입
     * [POST] /api/v1/crews/{crewId}/join
     */
    @PostMapping("/{crewId}/join")
    fun joinCrew(
        @User memberId: Long?,
        @PathVariable crewId: Long,
    ): ApiResponse<Any> {
        crewService.joinCrew(memberId!!, crewId)
        return ApiResponse.success()
    }
}
