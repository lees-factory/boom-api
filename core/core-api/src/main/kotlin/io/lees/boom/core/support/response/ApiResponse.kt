package io.lees.boom.core.support.response

import io.lees.boom.core.support.error.CoreApiErrorMessage
import io.lees.boom.core.support.error.CoreApiErrorType

class ApiResponse<T> private constructor(
    val result: ResultType,
    val data: T? = null,
    val error: CoreApiErrorMessage? = null,
) {
    companion object {
        fun success(): ApiResponse<Any> {
            return ApiResponse(ResultType.SUCCESS, null, null)
        }

        fun <S> success(data: S): ApiResponse<S> {
            return ApiResponse(ResultType.SUCCESS, data, null)
        }

        fun <S> error(error: CoreApiErrorType, errorData: Any? = null): ApiResponse<S> {
            return ApiResponse(ResultType.ERROR, null, CoreApiErrorMessage(error, errorData))
        }
    }
}
