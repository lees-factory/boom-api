package io.lees.boom.core.domain.member

import io.lees.boom.core.error.CoreErrorType
import io.lees.boom.core.error.CoreException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.UUID

@Service
class MemberService(
    private val memberFinder: MemberFinder,
    private val memberUpdater: MemberUpdater,
    private val imageStorage: ImageStorage,
) {
    fun getMe(memberId: Long): Member =
        memberFinder.findById(memberId)
            ?: throw CoreException(CoreErrorType.NOT_FOUND_MEMBER)

    fun getMember(memberId: Long): Member =
        memberFinder.findById(memberId)
            ?: throw CoreException(CoreErrorType.NOT_FOUND_MEMBER)

    @Transactional
    fun updateMe(
        memberId: Long,
        name: String?,
        email: String?,
        profileImageInput: ProfileImageInput?,
    ): Member {
        val member =
            memberFinder.findById(memberId)
                ?: throw CoreException(CoreErrorType.NOT_FOUND_MEMBER)

        val profileImageUrl =
            profileImageInput?.let {
                val path = "profile/$memberId/${UUID.randomUUID()}"
                imageStorage.upload(path, it.inputStream, it.contentType, it.contentLength)
            }

        val updatedMember =
            member.copy(
                name = name ?: member.name,
                email = if (email !== null) email else member.email,
                profileImage = profileImageUrl ?: member.profileImage,
            )

        return memberUpdater.update(updatedMember)
    }
}

data class ProfileImageInput(
    val inputStream: InputStream,
    val contentType: String,
    val contentLength: Long,
)
