package io.lees.boom.core.api.controller.v1.request

data class GymSearchRequest(
    // 남서쪽 (좌측 하단) 좌표
    val southWestLatitude: Double,
    val southWestLongitude: Double,

    // 북동쪽 (우측 상단) 좌표
    val northEastLatitude: Double,
    val northEastLongitude: Double,
)
