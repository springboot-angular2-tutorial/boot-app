package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.dto.request.PageParams
import com.myapp.repository.FeedRepository
import com.myapp.testing.TestMicropost
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertFailsWith

class FeedServiceTest {

    private val feedRepository: FeedRepository = mock()
    private val securityContextService: SecurityContextService = mock()
    private val feedService by lazy {
        FeedServiceImpl(
            feedRepository = feedRepository,
            securityContextService = securityContextService
        )
    }

    @Test
    fun `findFeed should find feed`() {
        `when`(securityContextService.currentUser())
            .doReturn(TestUser.copy(_id = 1))
        `when`(feedRepository.findFeed(1, PageParams()))
            .doReturn(listOf(
                TestMicropost.copy(
                    user = TestUser.copy(_id = 1)
                ),
                TestMicropost.copy(
                    user = TestUser.copy(_id = 2)
                )
            ))

        val feed = feedService.findFeed(PageParams())

        assertThat(feed.size).isEqualTo(2)
        assertThat(feed.first().isMyPost).isEqualTo(true)
        assertThat(feed.last().isMyPost).isEqualTo(false)
    }

    @Test
    fun `findFeed should throw when not signed in`() {
        assertFailsWith<AccessDeniedException> {
            feedService.findFeed(PageParams())
        }
    }

}
