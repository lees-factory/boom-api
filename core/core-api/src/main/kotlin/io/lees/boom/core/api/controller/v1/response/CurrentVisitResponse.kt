package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.domain.CurrentVisit
import io.lees.boom.core.enums.CrowdLevel
import java.time.LocalDateTime

data class CurrentVisitResponse(
    val gymId: Long,
    val gymName: String,
    val gymAddress: String?,
    val latitude: Double,
    val longitude: Double,
    val maxCapacity: Int,
    val currentCount: Int,
    val crowdLevel: CrowdLevel,
    val admittedAt: LocalDateTime,
    val expiresAt: LocalDateTime,
) {
    companion object {
        fun of(currentVisit: CurrentVisit): CurrentVisitResponse =
            CurrentVisitResponse(
                gymId = currentVisit.gym.id!!,
                gymName = currentVisit.gym.name,
                gymAddress = currentVisit.gym.address,
                latitude = currentVisit.gym.location.latitude,
                longitude = currentVisit.gym.location.longitude,
                maxCapacity = currentVisit.gym.maxCapacity,
                currentCount = currentVisit.gym.currentCount,
                crowdLevel = currentVisit.gym.crowdLevel,
                admittedAt = currentVisit.activeVisit.admittedAt,
                expiresAt = currentVisit.activeVisit.expiresAt,
            )
    }
}
