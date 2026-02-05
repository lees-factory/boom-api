package io.lees.boom.core.support

/**
 * Domain 페이징 요청 객체
 * JPA Pageable과 분리된 도메인 모델
 */
data class PageRequest(
    val page: Int = 0,
    val size: Int = 10,
) {
    init {
        require(page >= 0) { "page must be >= 0" }
        require(size in 1..100) { "size must be 1~100" }
    }

    val offset: Long
        get() = page.toLong() * size
}