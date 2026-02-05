package io.lees.boom.core.support.error

import io.lees.boom.core.error.CoreErrorType

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

    constructor(coreErrorType: CoreErrorType, data: Any? = null) : this(
        code = coreErrorType.code.name,
        message = coreErrorType.message,
        data = data,
    )
}
