package io.lees.boom.core.support.error

class CoreApiException(
    val coreApiErrorType: CoreApiErrorType,
    val data: Any? = null,
) : RuntimeException(coreApiErrorType.message)
