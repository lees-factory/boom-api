package io.lees.boom.core.domain

import org.springframework.stereotype.Service

@Service
class GymService(
    private val gymReader: GymReader
) {

    fun getGymsOnMap(
        southWestLatitude: Double, southWestLongitude: Double,
        northEastLatitude: Double, northEastLongitude: Double
    ): List<Gym> {
        return gymReader.readInViewport(
            southWestLatitude, northEastLatitude,
            southWestLongitude, northEastLongitude
        )
    }
}
