package io.lees.boom.core.domain

data class MemberBlock(
    val id: Long? = null,
    val blockerId: Long,
    val blockedId: Long,
) {
    companion object {
        fun create(
            blockerId: Long,
            blockedId: Long,
        ): MemberBlock =
            MemberBlock(
                blockerId = blockerId,
                blockedId = blockedId,
            )
    }
}
