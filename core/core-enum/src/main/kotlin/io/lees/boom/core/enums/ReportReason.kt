package io.lees.boom.core.enums

enum class ReportReason(
    val description: String,
) {
    ABUSE_HARASSMENT("욕설, 비하 및 괴롭힘"),
    SPAM_COMMERCIAL("스팸 및 영리 목적의 홍보"),
    INAPPROPRIATE_CONTENT("부적절한 콘텐츠"),
}
