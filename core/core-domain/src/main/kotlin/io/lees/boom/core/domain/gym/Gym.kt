package io.lees.boom.core.domain.gym

import io.lees.boom.core.enums.CrowdLevel

data class Gym(
    val id: Long? = null,
    val name: String,
    val address: String? = null,
    val location: Location,
    val maxCapacity: Int, // [추가]
    val currentCount: Int, // [추가]
    val crowdLevel: CrowdLevel = CrowdLevel.NORMAL,
) {
    companion object {
        fun create(
            name: String,
            address: String?,
            location: Location,
            maxCapacity: Int = 50,
            currentCount: Int = 0,
            crowdLevel: CrowdLevel = CrowdLevel.NORMAL,
        ): Gym =
            Gym(
                name = name,
                address = address,
                location = location,
                maxCapacity = maxCapacity,
                currentCount = currentCount,
                crowdLevel = crowdLevel,
            )
    }
}
