package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.CrewCreateRequest
import io.lees.boom.core.api.controller.v1.request.CrewScheduleCreateRequest
import io.lees.boom.core.api.controller.v1.response.CrewIdResponse
import io.lees.boom.core.api.controller.v1.response.CrewMemberResponse
import io.lees.boom.core.api.controller.v1.response.CrewRankingResponse
import io.lees.boom.core.api.controller.v1.response.CrewResponse
import io.lees.boom.core.api.controller.v1.response.CrewScheduleResponse
import io.lees.boom.core.api.controller.v1.response.MyCrewResponse
import io.lees.boom.core.api.controller.v1.response.ScheduleParticipantResponse
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
                crewImage = request.crewImage,
                maxMemberCount = request.maxMemberCount ?: 100,
                latitude = request.latitude,
                longitude = request.longitude,
                address = request.address,
            )
        return ApiResponse.success(CrewIdResponse(createdCrew.id!!))
    }

    /**
     * 크루 상세 조회
     * [GET] /api/v1/crews/{crewId}
     * 내 크루 상세 / 동네 크루 상세 모두 이 API로 조회
     */
    @GetMapping("/{crewId}")
    fun getCrew(
        @PathVariable crewId: Long,
    ): ApiResponse<CrewResponse> {
        val crew = crewService.getCrew(crewId)
        return ApiResponse.success(CrewResponse.of(crew))
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

    /**
     * 크루 일정 참여
     * [POST] /api/v1/crews/{crewId}/schedules/{scheduleId}/participate
     */
    @PostMapping("/{crewId}/schedules/{scheduleId}/participate")
    fun participateSchedule(
        @User memberId: Long?,
        @PathVariable crewId: Long,
        @PathVariable scheduleId: Long,
    ): ApiResponse<Any> {
        crewService.participateSchedule(crewId, scheduleId, memberId!!)
        return ApiResponse.success()
    }

    /**
     * 크루 일정 참여자 목록 조회
     * [GET] /api/v1/crews/{crewId}/schedules/{scheduleId}/participants
     */
    @GetMapping("/{crewId}/schedules/{scheduleId}/participants")
    fun getScheduleParticipants(
        @User memberId: Long?,
        @PathVariable crewId: Long,
        @PathVariable scheduleId: Long,
    ): ApiResponse<List<ScheduleParticipantResponse>> {
        val participants = crewService.getScheduleParticipants(crewId, scheduleId, memberId!!)
        return ApiResponse.success(participants.map { ScheduleParticipantResponse.from(it) })
    }

    /**
     * 동네 크루 찾기
     * [GET] /api/v1/crews/local?lat=37.123&lon=127.123
     */
    @GetMapping("/local")
    fun getLocalCrews(
        @RequestParam lat: Double,
        @RequestParam lon: Double,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<List<CrewResponse>> {
        val crews = crewService.getLocalCrews(lat, lon, page, size)
        // CrewResponse.from(it) 팩토리 메서드 필요
        return ApiResponse.success(crews.map { CrewResponse.of(it) })
    }

    /**
     * 크루 랭킹 조회 (크루원 평균 활동 점수 기반)
     * [GET] /api/v1/crews/ranking
     */
    @GetMapping("/ranking")
    fun getCrewRanking(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<List<CrewRankingResponse>> {
        val rankings = crewService.getCrewRanking(page, size)
        return ApiResponse.success(rankings.map { CrewRankingResponse.from(it) })
    }
}
