package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.Relationship
import com.myapp.domain.User
import com.myapp.dto.page.PageImpl
import com.myapp.dto.request.UserEditParams
import com.myapp.dto.request.UserNewParams
import com.myapp.repository.RelationshipRepository
import com.myapp.repository.UserRepository
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertFailsWith


class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val relationshipRepository: RelationshipRepository = mock()
    private val securityContextService: SecurityContextService = mock()
    private val userService by lazy {
        UserServiceImpl(
            userRepository = userRepository,
            relationshipRepository = relationshipRepository,
            securityContextService = securityContextService
        )
    }

    @Test
    fun `findOne should find a user who is followed by current user`() {
        val currentUser = TestUser.copy(_id = 1)
        val user = TestUser.copy(_id = 2)
        `when`(securityContextService.currentUser())
            .doReturn(currentUser)
        `when`(userRepository.findOneWithStats(2))
            .doReturn(user)
        `when`(relationshipRepository.findOneByFollowerAndFollowed(1, 2))
            .doReturn(Relationship(
                follower = currentUser,
                followed = user
            ))

        userService.findOne(2).apply {
            assertThat(id).isEqualTo(2)
            assertThat(isFollowedByMe).isEqualTo(true)
            assertThat(isMyself).isEqualTo(false)
        }
    }

    @Test
    fun `findOne should find a user who is current user itself`() {
        val currentUser = TestUser.copy(_id = 1)
        val user = currentUser
        `when`(securityContextService.currentUser())
            .doReturn(currentUser)
        `when`(userRepository.findOneWithStats(1))
            .doReturn(user)

        userService.findOne(1).apply {
            assertThat(id).isEqualTo(1)
            assertThat(isMyself).isEqualTo(true)
            assertThat(isFollowedByMe).isEqualTo(false)
        }
    }

    @Test
    fun `findOne should find a user when not signed in`() {
        `when`(userRepository.findOneWithStats(1))
            .doReturn(TestUser.copy(_id = 1))

        userService.findOne(1).apply {
            assertThat(id).isEqualTo(1)
            assertThat(isMyself).isNull()
            assertThat(isFollowedByMe).isNull()
        }
    }

    @Test
    fun `findMe should find me`() {
        val currentUser = TestUser.copy(_id = 1)
        val user = currentUser
        `when`(securityContextService.currentUser())
            .doReturn(currentUser)
        `when`(userRepository.findOneWithStats(1))
            .doReturn(user)

        userService.findMe().apply {
            assertThat(id).isEqualTo(1)
            assertThat(isMyself).isEqualTo(true)
            assertThat(isFollowedByMe).isEqualTo(false)
        }
    }

    @Test
    fun `findMe should throw when not signed in`() {
        assertFailsWith<AccessDeniedException> {
            userService.findMe()
        }
    }

    @Test
    fun `findAll should find users`() {
        val page = PageImpl(
            content = listOf(TestUser),
            totalPages = 1
        )
        `when`(userRepository.findAll(1, 1)).doReturn(page)

        userService.findAll(1, 1).apply {
            assertThat(this).isEqualTo(page)
        }
    }

    @Test
    fun `create should create a user`() {
        userService.create(UserNewParams(
            email = "test1@test.com",
            password = "secret123",
            name = "John Doe"
        ))

        argumentCaptor<User>().apply {
            verify(userRepository).create(capture())
            assertThat(firstValue.username).isEqualTo("test1@test.com")
            assertThat(firstValue.password).matches("^\\$2[ayb]\\$.{56}$")
            assertThat(firstValue.name).isEqualTo("John Doe")
        }
    }

    @Test
    fun `updateMe should update me`() {
        `when`(securityContextService.currentUser()).doReturn(TestUser)

        userService.updateMe(UserEditParams(
            email = "test1@test.com",
            password = "secret123",
            name = "John Doe"
        ))

        argumentCaptor<User>().apply {
            verify(userRepository).update(capture())
            assertThat(firstValue.username).isEqualTo("test1@test.com")
            assertThat(firstValue.password).matches("^\\$2[ayb]\\$.{56}$")
            assertThat(firstValue.name).isEqualTo("John Doe")
        }
    }

    @Test
    fun `updateMe should update me with blank parameters`() {
        `when`(securityContextService.currentUser()).doReturn(TestUser)

        userService.updateMe(UserEditParams())

        argumentCaptor<User>().apply {
            verify(userRepository).update(capture())
            assertThat(firstValue.username).isEqualTo(TestUser.username)
            assertThat(firstValue.password).isEqualTo(TestUser.password)
            assertThat(firstValue.name).isEqualTo(TestUser.name)
        }
    }

    @Test
    fun `updateMe should throw when not signed in`() {
        assertFailsWith<AccessDeniedException> {
            userService.updateMe(UserEditParams())
        }
    }

}