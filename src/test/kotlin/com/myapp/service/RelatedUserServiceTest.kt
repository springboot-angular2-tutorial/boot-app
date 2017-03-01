package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.Relationship
import com.myapp.dto.request.PageParams
import com.myapp.repository.RelatedUserRepository
import com.myapp.repository.RelationshipRepository
import com.myapp.testing.TestRelatedUser
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`

class RelatedUserServiceTest {

    private val relatedUserRepository: RelatedUserRepository = mock()
    private val relationshipRepository: RelationshipRepository = mock()
    private val securityContextService: SecurityContextService = mock()
    private val relatedUserService by lazy {
        RelatedUserServiceImpl(
            relatedUserRepository = relatedUserRepository,
            relationshipRepository = relationshipRepository,
            securityContextService = securityContextService
        )
    }

    @Test
    fun `findFollowers should find followers`() {
        val user = TestUser.copy(_id = 1)
        val currentUser = TestUser.copy(_id = 2)
        val followers = listOf(
            TestRelatedUser.copy(_id = 101),
            TestRelatedUser.copy(_id = 102)
        )
        val currentUserRelationships = listOf(
            Relationship(follower = currentUser, followed = TestUser.copy(_id = 101))
        )
        `when`(securityContextService.currentUser())
            .doReturn(currentUser)
        `when`(relatedUserRepository.findFollowers(user.id, PageParams()))
            .doReturn(followers)
        `when`(relationshipRepository.findAllByFollowerAndFollowedUsers(currentUser.id, followers.map { it.id }))
            .doReturn(currentUserRelationships)

        val foundFollowers = relatedUserService.findFollowers(user.id, PageParams())

        assertThat(foundFollowers.size).isEqualTo(2)
        assertThat(foundFollowers.first().isFollowedByMe).isEqualTo(true)
        assertThat(foundFollowers.last().isFollowedByMe).isEqualTo(false)
    }

    @Test
    fun `findFollowers should find followers when not signed in`() {
        val user = TestUser.copy(_id = 1)
        val followers = listOf(TestRelatedUser.copy(_id = 101))
        `when`(relatedUserRepository.findFollowers(user.id, PageParams()))
            .doReturn(followers)

        val foundFollowers = relatedUserService.findFollowers(user.id, PageParams())

        assertThat(foundFollowers.size).isEqualTo(1)
        assertThat(foundFollowers.first().isFollowedByMe).isNull()
    }

    @Test
    fun `findFollowings should find followings`() {
        val user = TestUser.copy(_id = 1)
        val currentUser = TestUser.copy(_id = 2)
        val followings = listOf(
            TestRelatedUser.copy(_id = 101),
            TestRelatedUser.copy(_id = 102)
        )
        val currentUserRelationships = listOf(
            Relationship(follower = currentUser, followed = TestUser.copy(_id = 101))
        )
        `when`(securityContextService.currentUser())
            .doReturn(currentUser)
        `when`(relatedUserRepository.findFollowings(user.id, PageParams()))
            .doReturn(followings)
        `when`(relationshipRepository.findAllByFollowerAndFollowedUsers(currentUser.id, followings.map { it.id }))
            .doReturn(currentUserRelationships)

        val foundFollowings = relatedUserService.findFollowings(user.id, PageParams())

        assertThat(foundFollowings.size).isEqualTo(2)
        assertThat(foundFollowings.first().isFollowedByMe).isEqualTo(true)
        assertThat(foundFollowings.last().isFollowedByMe).isEqualTo(false)
    }

    @Test
    fun `findFollowings should find followings when not signed in`() {
        val user = TestUser.copy(_id = 1)
        val followings = listOf(TestRelatedUser.copy(_id = 101))
        `when`(relatedUserRepository.findFollowings(user.id, PageParams()))
            .doReturn(followings)

        val foundFollowings = relatedUserService.findFollowings(user.id, PageParams())

        assertThat(foundFollowings.size).isEqualTo(1)
        assertThat(foundFollowings.first().isFollowedByMe).isNull()
    }

}