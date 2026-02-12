package io.lees.boom.core.api.controller.v1

import io.lees.boom.core.api.controller.v1.request.NotificationSettingUpdateRequest
import io.lees.boom.core.api.controller.v1.request.PushTokenUpdateRequest
import io.lees.boom.core.api.controller.v1.response.NotificationSettingResponse
import io.lees.boom.core.domain.NotificationSettingService
import io.lees.boom.core.support.User
import io.lees.boom.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members/me")
class NotificationSettingController(
    private val notificationSettingService: NotificationSettingService,
) {
    @GetMapping("/notification-setting")
    fun getNotificationSetting(
        @User memberId: Long?,
    ): ApiResponse<NotificationSettingResponse> {
        val setting = notificationSettingService.getOrCreateSetting(memberId!!)
        return ApiResponse.success(NotificationSettingResponse.from(setting))
    }

    @PutMapping("/notification-setting")
    fun updateNotificationSetting(
        @User memberId: Long?,
        @RequestBody request: NotificationSettingUpdateRequest,
    ): ApiResponse<Any> {
        notificationSettingService.updateSetting(
            memberId = memberId!!,
            pushEnabled = request.pushEnabled,
            crewScheduleEnabled = request.crewScheduleEnabled,
            crewChatEnabled = request.crewChatEnabled,
        )
        return ApiResponse.success()
    }

    @PutMapping("/push-token")
    fun updatePushToken(
        @User memberId: Long?,
        @RequestBody request: PushTokenUpdateRequest,
    ): ApiResponse<Any> {
        notificationSettingService.updatePushToken(
            memberId = memberId!!,
            pushToken = request.pushToken,
        )
        return ApiResponse.success()
    }
}
