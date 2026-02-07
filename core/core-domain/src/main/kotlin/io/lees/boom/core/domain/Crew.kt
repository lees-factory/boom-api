package io.lees.boom.core.domain

data class Crew(
    val id: Long? = null,
    val name: String,
    val description: String,
    val crewImage: String? = null,
    val maxMemberCount: Int = 100,
    val memberCount: Int = 0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val activityScore: Double = 0.0,
) {
    companion object {
        fun create(
            name: String,
            description: String,
            crewImage: String?,
            maxMemberCount: Int,
            latitude: Double?,
            longitude: Double?,
            address: String?,
        ): Crew =
            Crew(
                name = name,
                description = description,
                crewImage = crewImage,
                maxMemberCount = maxMemberCount,
                latitude = latitude,
                longitude = longitude,
                address = address,
            )
    }
}
