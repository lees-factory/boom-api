package io.lees.boom.core.error

class CoreException(
    val errorType: CoreErrorType,
    val data: Any? = null,
    override val message: String = errorType.message,
) : RuntimeException(message) {
    constructor(errorType: CoreErrorType) : this(errorType, null, errorType.message)

    constructor(errorType: CoreErrorType, customMessage: String) : this(errorType, null, customMessage)

    constructor(errorType: CoreErrorType, data: Any?) : this(errorType, data, errorType.message)
}