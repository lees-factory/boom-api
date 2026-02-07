package io.lees.boom.core.enums

enum class BadgeType(
    val category: BadgeCategory,
    val title: String,
    val emoji: String,
    val description: String,
    val threshold: Int,
) {
    // 암장출석 누적
    ATTEND_10(BadgeCategory.ATTENDANCE, "초크 묻었다", "\uD83E\uDEA8", "출석 10회", 10),
    ATTEND_30(BadgeCategory.ATTENDANCE, "벽이 익숙해졌다", "\uD83E\uDDD7", "출석 30회", 30),
    ATTEND_100(BadgeCategory.ATTENDANCE, "여기 집이야", "\uD83C\uDFE0", "출석 100회", 100),
    ATTEND_300(BadgeCategory.ATTENDANCE, "거의 직원", "\uD83E\uDDD1\u200D\uD83D\uDCBC", "출석 300회", 300),
    ATTEND_999(BadgeCategory.ATTENDANCE, "999 클럽", "\uD83D\uDE08", "출석 999회", 999),

    // 연속 출석
    STREAK_7(BadgeCategory.STREAK, "끊기지 않는다", "\uD83D\uDD25", "7일 연속 출석", 7),
    STREAK_30(BadgeCategory.STREAK, "벽이 일상", "\uD83E\uDDF1", "30일 연속 출석", 30),

    // 주간 루틴
    WEEK_3(BadgeCategory.WEEKLY, "주 3은 기본", "\uD83D\uDCC5", "주 3일 출석", 3),
    WEEK_5(BadgeCategory.WEEKLY, "주 5일 벽친자", "\uD83E\uDDD7\u200D\u2642\uFE0F", "주 5일 출석", 5),
    WEEK_7(BadgeCategory.WEEKLY, "주 7일 지박령", "\uD83E\uDEA6", "주 7일 출석", 7),

    // 시간대
    EARLY(BadgeCategory.TIME, "일찍이", "\uD83C\uDF05", "05~09시 출석", 1),
    NIGHT(BadgeCategory.TIME, "올빼미", "\uD83C\uDF19", "22~02시 출석", 1),

    // 방문 패턴
    OPENER(BadgeCategory.VISIT_PATTERN, "오프너", "\uD83D\uDEAA", "오픈 30분 이내 입장", 1),
    WANDERER(BadgeCategory.VISIT_PATTERN, "떠돌이", "\uD83E\uDDF3", "48시간 내 2개 암장 방문", 1),
    ;

    companion object {
        fun byCategory(category: BadgeCategory): List<BadgeType> = entries.filter { it.category == category }
    }
}
