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


}