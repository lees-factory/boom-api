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

    // 짐 (Gym) - [추가]
    B4000, // ALREADY_ADMITTED (이미 입장 중)
    B4001, // NOT_ADMITTED (입장 기록 없음)
    B4002, // TOO_FAR_FROM_GYM (암장과 거리 초과)
}
