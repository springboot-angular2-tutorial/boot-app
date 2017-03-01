package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.RelatedUser
import com.myapp.dto.request.PageParams
import com.myapp.repository.RelatedUserRepository
import com.myapp.repository.RelationshipRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class RelatedUserServiceImpl(
    private val relatedUserRepository: RelatedUserRepository,
    private val relationshipRepository: RelationshipRepository,
    override val securityContextService: SecurityContextService
) : RelatedUserService, WithCurrentUser {

    override fun findFollowers(userId: Long, pageParams: PageParams): List<RelatedUser> {
        val relatedUsers = relatedUserRepository.findFollowers(userId, pageParams)
        val myRelationships = currentUser()?.let {
            val relatedUserIds = relatedUsers.map { it.id }.distinct()
            relationshipRepository.findAllByFollowerAndFollowedUsers(it.id, relatedUserIds)
        }

        return relatedUsers.map { relatedUser ->
            relatedUser.copy(
                isFollowedByMe = myRelationships?.any { it.followed.id == relatedUser.id }
            )
        }
    }

    override fun findFollowings(userId: Long, pageParams: PageParams): List<RelatedUser> {
        val relatedUsers = relatedUserRepository.findFollowings(userId, pageParams)
        val myRelationships = currentUser()?.let {
            val relatedUserIds = relatedUsers.map { it.id }.distinct()
            relationshipRepository.findAllByFollowerAndFollowedUsers(it.id, relatedUserIds)
        }

        return relatedUsers.map { relatedUser ->
            relatedUser.copy(
                isFollowedByMe = myRelationships?.any { it.followed.id == relatedUser.id }
            )
        }
    }

}