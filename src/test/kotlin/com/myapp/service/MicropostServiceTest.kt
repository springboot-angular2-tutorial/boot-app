package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.Micropost
import com.myapp.dto.request.PageParams
import com.myapp.repository.MicropostRepository
import com.myapp.service.exception.NotAuthorizedException
import com.myapp.testing.TestMicropost
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

class MicropostServiceTest {

    private val micropostRepository: MicropostRepository = mock()
    private val securityContextService: SecurityContextService = mock()
    private val micropostService by lazy {
        MicropostServiceImpl(
            micropostRepository = micropostRepository,
            securityContextService = securityContextService
        )
    }

    @Test
    fun `findAllByUser should find posts by user when signed in and the posts belongs to current user`() {
        `when`(securityContextService.currentUser())
            .doReturn(TestUser.copy(_id = 1))
        `when`(micropostRepository.findAllByUser(1, PageParams()))
            .doReturn(listOf(TestMicropost))

        micropostService.findAllByUser(1, PageParams()).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().isMyPost).isEqualTo(true)
        }
    }

    @Test
    fun `findAllByUser should find posts by user when signed in and the posts belongs to another user`() {
        `when`(securityContextService.currentUser())
            .doReturn(TestUser.copy(_id = 1))
        `when`(micropostRepository.findAllByUser(2, PageParams()))
            .doReturn(listOf(TestMicropost))

        micropostService.findAllByUser(2, PageParams()).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().isMyPost).isEqualTo(false)
        }
    }

    @Test
    fun `findAllByUser should find posts by user when not signed`() {
        `when`(micropostRepository.findAllByUser(1, PageParams()))
            .doReturn(listOf(TestMicropost))

        micropostService.findAllByUser(1, PageParams()).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().isMyPost).isNull()
        }
    }

    @Test
    fun `findMyPosts should find my posts`() {
        `when`(securityContextService.currentUser())
            .doReturn(TestUser.copy(_id = 1))
        `when`(micropostRepository.findAllByUser(1, PageParams()))
            .doReturn(listOf(TestMicropost))

        micropostService.findMyPosts(PageParams()).apply {
            assertThat(size).isEqualTo(1)
            assertThat(first().isMyPost).isEqualTo(true)
        }
    }

    @Test
    fun `findMyPosts should throw when not signed in`() {
        assertFailsWith<AccessDeniedException> {
            micropostService.findMyPosts(PageParams())
        }
    }

    @Test
    fun `create should create a post`() {
        `when`(securityContextService.currentUser()).doReturn(TestUser)

        micropostService.create("my test content")

        argumentCaptor<Micropost>().apply {
            verify(micropostRepository).create(capture())
            assertThat(firstValue.content).isEqualTo("my test content")
            assertThat(firstValue.user).isEqualTo(TestUser)
        }
    }

    @Test
    fun `create should throw when not signed in`() {
        assertFailsWith<AccessDeniedException> {
            micropostService.create("my test content")
        }
    }

    @Test
    fun `delete should delete a post`() {
        val currentUser = TestUser.copy(_id = 1)
        `when`(securityContextService.currentUser())
            .doReturn(TestUser.copy(_id = 1))
        `when`(micropostRepository.findOne(101))
            .doReturn(TestMicropost.copy(
                user = currentUser
            ))

        micropostService.delete(101)

        verify(micropostRepository).delete(101)
    }

    @Test
    fun `delete should not delete a post when the post does not belong to current user`() {
        `when`(securityContextService.currentUser())
            .doReturn(TestUser.copy(_id = 1))
        `when`(micropostRepository.findOne(101))
            .doReturn(TestMicropost.copy(
                user = TestUser.copy(_id = 2)
            ))

        assertFailsWith<NotAuthorizedException> {
            micropostService.delete(101)
        }
    }

    @Test
    fun `delete should throw when not signed in`() {
        assertFailsWith<AccessDeniedException> {
            micropostService.delete(1)
        }
    }

}