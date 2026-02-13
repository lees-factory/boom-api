package io.lees.boom.storage.db.core

interface CrewRankingProjection {
    val crewId: Long
    val name: String
    val description: String
    val memberCount: Int
    val maxMemberCount: Int
    val avgScore: Double
    val latitude: Double?
    val longitude: Double?
    val address: String?
}
