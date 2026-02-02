package io.lees.boom.core.api.controller.v1.request

data class GymRadiusSearchRequest(
    val latitude: Double,
    val longitude: Double,
    val radiusKm: Double,
)
