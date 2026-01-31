package io.lees.boom.core.domain

data class Crew(
    val id: Long? = null,
    val name: String,
    val description: String,
    val maxMemberCount: Int = 30, // 기획안 예시 따름
    // val region: Region, // 추후 지역 기반 검색을 위한 지역 정보 필요
) {
    companion object {
        fun create(
            name: String,
            description: String,
        ): Crew =
            Crew(
                name = name,
                description = description,
            )
    }
}
