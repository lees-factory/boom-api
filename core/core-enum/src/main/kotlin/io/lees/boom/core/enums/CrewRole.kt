package io.lees.boom.core.enums

enum class CrewRole(
    val description: String,
) {
    LEADER("크루장"), // 크루 삭제, 멤버 강퇴, 정보 수정 권한
    MEMBER("크루원"), // 일반 활동 권한
    GUEST("게스트"), // (확장성 고려) 참관, 임시 권한
}
