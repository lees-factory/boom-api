package io.lees.boom.core.domain

data class Crew(
    val id: Long? = null,
    val name: String,
    val description: String,
    val maxMemberCount: Int = 100, // [수정] 기본값 100으로 변경
) {
    companion object {
        fun create(
            name: String,
            description: String,
            maxMemberCount: Int, // [추가]
        ): Crew =
            Crew(
                name = name,
                description = description,
                maxMemberCount = maxMemberCount,
            )
    }
}
