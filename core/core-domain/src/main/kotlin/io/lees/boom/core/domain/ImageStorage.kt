package io.lees.boom.core.domain

import java.io.InputStream

interface ImageStorage {
    fun upload(
        path: String,
        inputStream: InputStream,
        contentType: String,
        contentLength: Long,
    ): String
}
