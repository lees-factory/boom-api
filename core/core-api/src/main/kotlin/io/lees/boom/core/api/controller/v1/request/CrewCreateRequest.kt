package io.lees.boom.core.api.controller.v1.request

data class CrewCreateRequest(
    val name: String,
    val description: String,
    val crewImage: String? = null,
    val maxMemberCount: Int? = 100,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
)
