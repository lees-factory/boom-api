package io.lees.boom.core.error

enum class CoreErrorType(
    val kind: CoreErrorKind,
    val code: CoreErrorCode,
    val message: String,
    val level: CoreErrorLevel,
) {
    // 공통
    NOT_FOUND_DATA(CoreErrorKind.SERVER_ERROR, CoreErrorCode.B1000, "해당 데이터를 찾지 못했습니다.", CoreErrorLevel.INFO),
    INVALID_REQUEST(CoreErrorKind.SERVER_ERROR, CoreErrorCode.B1001, "요청이 올바르지 않습니다.", CoreErrorLevel.INFO),

    // 멤버

    // 인증
    UNAUTHORIZED_USER(CoreErrorKind.UNAUTHORIZED, CoreErrorCode.B2000, "인증되지 않는 사용자 입니다.", CoreErrorLevel.ERROR),
    INVALID_USERID(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.B2001, "잘못된 사용자 ID 형식입니다.", CoreErrorLevel.ERROR),
    NOT_FOUND_MEMBER(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.B2002, "회원을 찾을 수 없습니다.", CoreErrorLevel.ERROR),

    // 크루
    CREW_CREATE_ERROR(CoreErrorKind.SERVER_ERROR, CoreErrorCode.B3000, "크루 생성 실패", CoreErrorLevel.ERROR),
    CREW_NOT_FOUND(
        CoreErrorKind.CLIENT_ERROR,
        CoreErrorCode.B3001,
        "크루를 찾을 수 없습니다.",
        CoreErrorLevel.INFO,
    ),
    CREW_MEMBER_NOT_AUTHORIZED(
        CoreErrorKind.CLIENT_ERROR,
        CoreErrorCode.B3002,
        "크루 멤버가 아니거나 권한이 없습니다.",
        CoreErrorLevel.INFO,
    ),

    ALREADY_ADMITTED(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.B4000, "이미 입장 중인 상태입니다.", CoreErrorLevel.INFO),
    NOT_ADMITTED(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.B4001, "입장 기록이 없습니다.", CoreErrorLevel.INFO),
    TOO_FAR_FROM_GYM(
        CoreErrorKind.CLIENT_ERROR,
        CoreErrorCode.B4002,
        "암장에서 너무 멀리 있습니다. 100m 이내에서 입장해주세요.",
        CoreErrorLevel.INFO,
    ),
}
