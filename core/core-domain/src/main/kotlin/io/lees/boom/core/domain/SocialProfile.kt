package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider

data class SocialProfile(
    val provider: SocialProvider,
    val socialId: String,
    val email: String,
    val name: String,
)
