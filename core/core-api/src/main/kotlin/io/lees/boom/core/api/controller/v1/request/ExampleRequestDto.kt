package io.lees.boom.core.api.controller.v1.request

import io.lees.boom.core.domain.ExampleData

data class ExampleRequestDto(
    val data: String,
) {
    fun toExampleData(): ExampleData {
        return ExampleData(data, data)
    }
}
