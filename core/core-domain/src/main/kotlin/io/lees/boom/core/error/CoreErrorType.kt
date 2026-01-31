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

    // 인등

    UNAUTHORIZED_USER(CoreErrorKind.SERVER_ERROR, CoreErrorCode.B2000, "인증되지 않는 사용자 입니다.", CoreErrorLevel.ERROR),
    INVALID_USERID(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.B2001, "잘못된 사용자 ID 형식입니다.", CoreErrorLevel.ERROR),

    // 크루
    CREW_CREATE_ERROR(CoreErrorKind.SERVER_ERROR, CoreErrorCode.B3000, "크루 생성 실패", CoreErrorLevel.ERROR),
}
