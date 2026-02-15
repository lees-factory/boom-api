package io.lees.boom.core.domain.gym

import io.lees.boom.core.support.PageRequest
import io.lees.boom.core.support.SliceResult

interface GymRepository {
    fun save(gym: Gym): Gym

    fun findById(id: Long): Gym?

    fun findGymsWithinViewport(
        southWestLocation: Location,
        northEastLocation: Location,
    ): List<Gym>

    fun findGymsWithinViewportSlice(
        southWestLocation: Location,
        northEastLocation: Location,
        pageRequest: PageRequest,
    ): SliceResult<Gym>
}
