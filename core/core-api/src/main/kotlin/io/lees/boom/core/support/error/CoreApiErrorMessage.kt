package io.lees.boom.core.support.error

class CoreApiErrorMessage private constructor(
    val code: String,
    val message: String,
    val data: Any? = null,
) {
    constructor(coreApiErrorType: CoreApiErrorType, data: Any? = null) : this(
        code = coreApiErrorType.code.name,
        message = coreApiErrorType.message,
        data = data,
    )
}
