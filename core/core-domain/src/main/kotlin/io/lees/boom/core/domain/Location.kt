package io.lees.boom.core.domain

data class Location(
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        fun create(latitude: Double, longitude: Double): Location =
            Location(latitude, longitude)
    }
}
