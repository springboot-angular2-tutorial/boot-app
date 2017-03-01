package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.Relationship
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import com.myapp.service.exception.RelationshipNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class RelationshipServiceImpl(
    private val userRepository: UserRepository,
    private val relationshipRepository: RelationshipRepository,
    override val securityContextService: SecurityContextService
) : RelationshipService, WithCurrentUser {

    @Suppress("unused")
    private val logger = LoggerFactory.getLogger(RelationshipServiceImpl::class.java)

    override fun follow(userId: Long) {
        val user = userRepository.findOne(userId)
        val currentUser = currentUserOrThrow()
        val relationship = Relationship(
            follower = currentUser,
            followed = user
        )
        relationshipRepository.create(relationship)
    }

    override fun unfollow(userId: Long) {
        val currentUser = currentUserOrThrow()
        val relationship = relationshipRepository
            .findOneByFollowerAndFollowed(currentUser.id, userId)

        relationship?.let {
            relationshipRepository.delete(it.id)
        } ?: throw RelationshipNotFoundException()
    }

}