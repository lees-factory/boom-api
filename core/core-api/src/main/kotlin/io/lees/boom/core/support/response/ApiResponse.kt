package io.lees.boom.core.support.response

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.support.error.CoreApiErrorMessage
import io.lees.boom.core.support.error.CoreApiErrorType

class ApiResponse<T> private constructor(
    val result: ResultType,
    val data: T? = null,
    val error: CoreApiErrorMessage? = null,
) {
    companion object {
        fun success(): ApiResponse<Any> = ApiResponse(ResultType.SUCCESS, null, null)

        fun <S> success(data: S): ApiResponse<S> = ApiResponse(ResultType.SUCCESS, data, null)

        fun <S> error(
            error: CoreApiErrorType,
            errorData: Any? = null,
        ): ApiResponse<S> = ApiResponse(ResultType.ERROR, null, CoreApiErrorMessage(error, errorData))

        fun <S> error(
            error: CoreErrorType,
            errorData: Any? = null,
        ): ApiResponse<S> = ApiResponse(ResultType.ERROR, null, CoreApiErrorMessage(error, errorData))
    }
}
