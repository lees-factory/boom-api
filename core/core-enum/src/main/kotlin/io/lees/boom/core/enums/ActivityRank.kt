package io.lees.boom.core.enums

/**
 * 3.4. 활동 기여도 기반 '컬러 랭킹' (Anti-Goinmul)
 * 클라이밍 홀드 색상을 오마주한 계급 체계
 */
enum class ActivityRank(
    val description: String,
    val minScore: Int,
    val colorHex: String,
) {
    YELLOW("노랑단", 0, "#FFD700"), // 시작
    GREEN("초록단", 100, "#008000"), // 입문
    RED("빨강단", 300, "#FF0000"), // 숙련
    PURPLE("보라단", 600, "#800080"), // 고수
    GOLD("황금단", 1000, "#FFD700"), // 마스터
    ;

    companion object {
        fun fromScore(score: Int): ActivityRank =
            entries
                .sortedByDescending { it.minScore }
                .firstOrNull { score >= it.minScore }
                ?: YELLOW
    }
}
