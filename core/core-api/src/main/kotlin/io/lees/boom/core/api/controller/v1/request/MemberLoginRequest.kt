package io.lees.boom.core.api.controller.v1.request

import io.lees.boom.core.enums.SocialProvider

data class MemberLoginRequest(
    val provider: SocialProvider,
    val socialId: String,
    val name: String? = null,
    val email: String? = null,
    val profileImage: String? = null,
)
