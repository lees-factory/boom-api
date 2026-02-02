package io.lees.boom.core.domain

import org.springframework.stereotype.Component
import kotlin.math.cos

@Component // 스프링 빈으로 등록하여 주입받아 사용
class LocationCalculator {
    fun calculateViewport(
        center: Location,
        radiusKm: Double,
    ): Viewport {
        val latChange = radiusKm / 111.0
        val lonChange = radiusKm / (111.0 * cos(Math.toRadians(center.latitude)))

        val southWest = Location.create(center.latitude - latChange, center.longitude - lonChange)
        val northEast = Location.create(center.latitude + latChange, center.longitude + lonChange)

        return Viewport(southWest, northEast)
    }
}
