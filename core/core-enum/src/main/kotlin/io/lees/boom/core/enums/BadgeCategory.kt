package io.lees.boom.core.enums

enum class BadgeCategory(
    val description: String,
) {
    ATTENDANCE("암장출석 누적"),
    STREAK("연속 출석"),
    WEEKLY("주간 루틴"),
    TIME("시간대"),
    VISIT_PATTERN("방문 패턴"),
}
