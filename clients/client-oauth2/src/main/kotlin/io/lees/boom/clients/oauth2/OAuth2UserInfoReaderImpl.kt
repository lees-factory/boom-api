package io.lees.boom.clients.oauth2

import io.lees.boom.core.domain.OAuth2UserInfo
import io.lees.boom.core.domain.OAuth2UserInfoReader
import org.springframework.stereotype.Component

/**
 * OAuth2 사용자 정보 조회 구현체
 * Data Access Layer - core-domain의 인터페이스 구현
 */
@Component
class OAuth2UserInfoReaderImpl(
    oauth2Clients: List<OAuth2Client>,
) : OAuth2UserInfoReader {
    private val clientMap: Map<String, OAuth2Client> =
        oauth2Clients.associateBy { it.providerName }

    override fun read(
        provider: String,
        token: String,
    ): OAuth2UserInfo {
        val client =
            clientMap[provider]
                ?: throw IllegalArgumentException("Unsupported provider: $provider")

        val userInfo = client.getUserInfo(token)

        return OAuth2UserInfo(
            socialId = userInfo.socialId,
            email = userInfo.email,
            name = userInfo.name,
        )
    }
}
