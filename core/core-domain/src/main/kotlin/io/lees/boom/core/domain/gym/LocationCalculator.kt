package io.lees.boom.core.domain.gym

import org.springframework.stereotype.Component
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Component
class LocationCalculator {
    companion object {
        private const val EARTH_RADIUS_KM = 6371.0
    }

    /**
     * 두 지점 간 거리 계산 (Haversine 공식, 단위: km)
     */
    fun calculateDistanceKm(
        from: Location,
        to: Location,
    ): Double {
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)

        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * 두 지점이 지정된 반경(km) 이내인지 확인
     */
    fun isWithinRadius(
        from: Location,
        to: Location,
        radiusKm: Double,
    ): Boolean = calculateDistanceKm(from, to) <= radiusKm

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
