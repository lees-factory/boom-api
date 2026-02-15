package io.lees.boom.core.domain.member

import java.io.InputStream

interface ImageStorage {
    fun upload(
        path: String,
        inputStream: InputStream,
        contentType: String,
        contentLength: Long,
    ): String
}
