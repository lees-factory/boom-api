package io.lees.boom.core.error

enum class CoreErrorCode {
    // 공통
    B1000,
    B1001,
    B1002,

    // 인증
    B2000,
    B2001,
    B2002,

    // 크루
    B3000,
    B3001,
    B3002,

    // 크루 가입
    B3003, // CREW_ALREADY_JOINED (이미 가입된 크루)
    B3004, // CREW_MEMBER_LIMIT_EXCEEDED (크루 정원 초과)

    // 크루 일정 참여
    B3100, // SCHEDULE_NOT_FOUND
    B3101, // SCHEDULE_ALREADY_PARTICIPATED

    // 짐 (Gym) - [추가]
    B4000, // ALREADY_ADMITTED (이미 입장 중)
    B4001, // NOT_ADMITTED (입장 기록 없음)
    B4002, // TOO_FAR_FROM_GYM (암장과 거리 초과)
}
