package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.ReportCreateRequest
import io.lees.boom.core.domain.report.ReportService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(
    private val reportService: ReportService,
) {
    /**
     * 신고하기
     * [POST] /api/v1/reports
     */
    @PostMapping
    fun createReport(
        @User memberId: Long?,
        @RequestBody request: ReportCreateRequest,
    ): ApiResponse<Any> {
        reportService.createReport(
            reporterId = memberId!!,
            targetType = request.targetType,
            targetId = request.targetId,
            reason = request.reason,
            description = request.description,
        )
        return ApiResponse.success()
    }
}
