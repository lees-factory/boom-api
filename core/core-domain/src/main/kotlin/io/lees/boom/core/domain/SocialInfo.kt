package io.lees.boom.core.domain

import io.lees.boom.core.enums.SocialProvider

data class SocialInfo(
    val provider: SocialProvider,
    val socialId: String,
)
