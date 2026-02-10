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

    // 크루 탈퇴/삭제
    B3005, // CREW_NOT_MEMBER (크루 멤버가 아님)
    B3006, // CREW_LEADER_CANNOT_LEAVE (리더는 탈퇴 불가, 크루 삭제 사용)
    B3007, // CREW_DELETE_NOT_ALLOWED (크루 삭제 불가 - 다른 멤버 존재)
    B3008, // CREW_MAX_MEMBER_COUNT_TOO_SMALL (최대 인원이 현재 인원보다 적음)

    // 크루 일정 참여
    B3100, // SCHEDULE_NOT_FOUND
    B3101, // SCHEDULE_ALREADY_PARTICIPATED
    B3102, // SCHEDULE_NOT_PARTICIPATED (일정 참여 기록 없음)
    B3103, // SCHEDULE_ALREADY_PASSED (이미 지난 일정)

    // 크루 채팅
    B3200, // CHAT_RATE_LIMIT_EXCEEDED (도배 방지)
    B3201, // CHAT_MESSAGE_NOT_FOUND

    // 짐 (Gym) - [추가]
    B4000, // ALREADY_ADMITTED (이미 입장 중)
    B4001, // NOT_ADMITTED (입장 기록 없음)
    B4002, // TOO_FAR_FROM_GYM (암장과 거리 초과)
}
