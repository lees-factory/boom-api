package io.lees.boom.core.support

/**
 * Domain Slice 모델 (JPA Slice와 분리)
 * 무한스크롤용 페이징 결과
 */
data class SliceResult<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
) {
    companion object {
        fun <T> of(
            content: List<T>,
            pageRequest: PageRequest,
            hasNext: Boolean,
        ): SliceResult<T> = SliceResult(content, pageRequest.page, pageRequest.size, hasNext)

        /**
         * limit+1 조회 결과로 SliceResult 생성
         */
        fun <T> fromLimitPlusOne(
            content: List<T>,
            pageRequest: PageRequest,
        ): SliceResult<T> {
            val hasNext = content.size > pageRequest.size
            val slicedContent = if (hasNext) content.dropLast(1) else content
            return SliceResult(slicedContent, pageRequest.page, pageRequest.size, hasNext)
        }
    }
}