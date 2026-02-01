package io.lees.boom.core.domain

import org.springframework.stereotype.Service

@Service
class GymService(
    private val gymReader: GymReader,
    private val locationCalculator: LocationCalculator,
) {
    fun getGymsOnMap(
        southWestLatitude: Double,
        southWestLongitude: Double,
        northEastLatitude: Double,
        northEastLongitude: Double,
    ): List<Gym> =
        gymReader.readInViewport(
            southWestLatitude,
            northEastLatitude,
            southWestLongitude,
            northEastLongitude,
        )

    fun getGymsByRadius(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
    ): List<Gym> {
        val center = Location.create(latitude, longitude)

        val viewport = locationCalculator.calculateViewport(center, radiusKm)

        return gymReader.readInViewport(
            southWestLatitude = viewport.southWest.latitude,
            northEastLatitude = viewport.northEast.latitude,
            southWestLongitude = viewport.southWest.longitude,
            northEastLongitude = viewport.northEast.longitude,
        )
    }
}
