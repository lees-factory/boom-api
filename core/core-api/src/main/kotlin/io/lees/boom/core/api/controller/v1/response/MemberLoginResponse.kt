package io.lees.boom.core.api.controller.v1.response

data class MemberLoginResponse(
    val accessToken: String,
    val refreshToken: String,
)
