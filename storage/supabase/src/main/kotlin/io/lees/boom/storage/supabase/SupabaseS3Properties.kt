package io.lees.boom.storage.supabase

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage.supabase")
data class SupabaseS3Properties(
    val endpoint: String,
    val region: String = "ap-northeast-2",
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val publicUrl: String,
)
