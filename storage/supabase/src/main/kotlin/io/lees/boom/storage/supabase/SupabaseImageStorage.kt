package io.lees.boom.storage.supabase

import io.lees.boom.core.domain.ImageStorage
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream

class SupabaseImageStorage(
    private val s3Client: S3Client,
    private val properties: SupabaseS3Properties,
) : ImageStorage {
    override fun upload(
        path: String,
        inputStream: InputStream,
        contentType: String,
        contentLength: Long,
    ): String {
        val putRequest =
            PutObjectRequest
                .builder()
                .bucket(properties.bucket)
                .key(path)
                .contentType(contentType)
                .build()

        s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, contentLength))

        return "${properties.publicUrl}/${properties.bucket}/$path"
    }
}
