package io.lees.boom.core.domain

import io.lees.boom.core.enums.CrowdLevel

data class Gym(
    val id: Long? = null,
    val name: String,
    val address: String? = null,

    val location: Location,

    val crowdLevel: CrowdLevel = CrowdLevel.NORMAL,
) {
    companion object {
        fun create(
            name: String,
            address: String?,
            location: Location,
            crowdLevel: CrowdLevel = CrowdLevel.NORMAL,
        ): Gym =
            Gym(
                name = name,
                address = address,
                location = location,
                crowdLevel = crowdLevel,
            )
    }
}
