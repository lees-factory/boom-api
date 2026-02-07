package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.CrewCreateRequest
import io.lees.boom.core.api.controller.v1.request.CrewScheduleCreateRequest
import io.lees.boom.core.api.controller.v1.response.CrewIdResponse
import io.lees.boom.core.api.controller.v1.response.CrewMemberResponse
import io.lees.boom.core.api.controller.v1.response.CrewScheduleResponse
import io.lees.boom.core.api.controller.v1.response.MyCrewResponse
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
     */
    @PostMapping
    fun createCrew(
        @User memberId: Long?,
        @RequestBody request: CrewCreateRequest,
    ): ApiResponse<CrewIdResponse> {
        val createdCrew =
            crewService.createCrew(
                memberId = memberId!!,
                name = request.name,
                description = request.description,
                maxMemberCount = request.maxMemberCount ?: 100,
            )
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

    /**
     * 내 크루 목록 조회
     * [GET] /api/v1/crews/my
     */
    @GetMapping("/my")
    fun getMyCrews(
        @User memberId: Long?,
    ): ApiResponse<List<MyCrewResponse>> {
        val myCrews = crewService.getMyCrews(memberId!!)
        return ApiResponse.success(myCrews.map { MyCrewResponse.from(it) })
    }

    /**
     * 크루 멤버 목록 조회
     * [GET] /api/v1/crews/{crewId}/members
     */
    @GetMapping("/{crewId}/members")
    fun getCrewMembers(
        @PathVariable crewId: Long,
    ): ApiResponse<List<CrewMemberResponse>> {
        val members = crewService.getCrewMembers(crewId)
        return ApiResponse.success(members.map { CrewMemberResponse.from(it) })
    }

    /**
     * 크루 일정 등록
     * [POST] /api/v1/crews/{crewId}/schedules
     */
    @PostMapping("/{crewId}/schedules")
    fun createSchedule(
        @User memberId: Long?,
        @PathVariable crewId: Long,
        @RequestBody request: CrewScheduleCreateRequest,
    ): ApiResponse<CrewScheduleResponse> {
        val schedule =
            crewService.createSchedule(
                crewId = crewId,
                memberId = memberId!!,
                gymId = request.gymId,
                title = request.title,
                description = request.description,
                scheduledAt = request.scheduledAt,
            )
        return ApiResponse.success(CrewScheduleResponse.from(schedule))
    }

    /**
     * 크루 일정 목록 조회
     * [GET] /api/v1/crews/{crewId}/schedules
     */
    @GetMapping("/{crewId}/schedules")
    fun getSchedules(
        @User memberId: Long?,
        @PathVariable crewId: Long,
    ): ApiResponse<List<CrewScheduleResponse>> {
        val schedules = crewService.getSchedules(crewId, memberId!!)
        return ApiResponse.success(schedules.map { CrewScheduleResponse.from(it) })
    }
}
