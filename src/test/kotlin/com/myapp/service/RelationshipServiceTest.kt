package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.Relationship
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import com.myapp.service.exception.RelationshipNotFoundException
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import kotlin.test.assertFailsWith

class RelationshipServiceTest {

    private val userRepository: UserRepository = mock()
    private val relationshipRepository: RelationshipRepository = mock()
    private val securityContextService: SecurityContextService = mock()
    private val relationshipService by lazy {
        RelationshipServiceImpl(
            userRepository = userRepository,
            relationshipRepository = relationshipRepository,
            securityContextService = securityContextService
        )
    }

    @Test
    fun `follow should follow user`() {
        val user = TestUser.copy()
        val currentUser = TestUser.copy()
        `when`(userRepository.findOne(1)).doReturn(user)
        `when`(securityContextService.currentUser()).doReturn(currentUser)

        relationshipService.follow(1)

        argumentCaptor<Relationship>().apply {
            verify(relationshipRepository).create(capture())
            assertThat(firstValue.follower).isEqualTo(currentUser)
            assertThat(firstValue.followed).isEqualTo(user)
        }
    }

    @Test
    fun `unfollow should unfollow user`() {
        val currentUser = TestUser.copy()
        `when`(securityContextService.currentUser()).doReturn(currentUser)
        `when`(relationshipRepository.findOneByFollowerAndFollowed(currentUser.id, 1))
            .doReturn(Relationship(
                _id = 101,
                follower = currentUser,
                followed = TestUser.copy()
            ))

        relationshipService.unfollow(1)

        verify(relationshipRepository).delete(101)
    }

    @Test
    fun `unfollow should not unfollow user when no relationship`() {
        val currentUser = TestUser.copy()
        `when`(securityContextService.currentUser()).doReturn(currentUser)

        assertFailsWith<RelationshipNotFoundException> {
            relationshipService.unfollow(1)
        }
    }

}