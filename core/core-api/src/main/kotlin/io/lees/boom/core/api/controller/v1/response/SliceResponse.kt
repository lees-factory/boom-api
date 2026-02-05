package io.lees.boom.core.api.controller.v1.response

import io.lees.boom.core.support.SliceResult

data class SliceResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
) {
    companion object {
        fun <T, R> of(
            sliceResult: SliceResult<T>,
            transform: (T) -> R,
        ): SliceResponse<R> =
            SliceResponse(
                content = sliceResult.content.map(transform),
                page = sliceResult.page,
                size = sliceResult.size,
                hasNext = sliceResult.hasNext,
            )
    }
}
