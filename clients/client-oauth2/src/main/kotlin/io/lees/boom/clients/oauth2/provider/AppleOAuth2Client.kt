package io.lees.boom.clients.oauth2.provider

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.lees.boom.clients.oauth2.InternalOAuth2UserInfo
import io.lees.boom.clients.oauth2.OAuth2Client
import org.springframework.stereotype.Component
import java.util.Base64

@Component
class AppleOAuth2Client : OAuth2Client {
    override val providerName: String = PROVIDER_NAME

    private val objectMapper = jacksonObjectMapper()

    override fun getUserInfo(accessToken: String): InternalOAuth2UserInfo {
        // Apple은 id_token(JWT)을 직접 파싱
        val payload = decodeJwtPayload(accessToken)

        return InternalOAuth2UserInfo(
            socialId = payload.sub,
            email = payload.email ?: "",
            name = "Apple User",
        )
    }

    private fun decodeJwtPayload(idToken: String): AppleIdTokenPayload {
        val parts = idToken.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid Apple id_token format")
        }

        val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))
        return objectMapper.readValue(payloadJson, AppleIdTokenPayload::class.java)
    }

    private data class AppleIdTokenPayload(
        val sub: String,
        val email: String?,
    )

    companion object {
        const val PROVIDER_NAME = "APPLE"
    }
}
