package com.myapp.controller

import com.myapp.service.FeedService
import com.myapp.testing.TestMicropost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import org.hamcrest.Matchers.*
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(FeedController::class)
class FeedControllerTest : BaseControllerTest() {

    @MockBean
    lateinit private var feedService: FeedService

    @Test
    fun `findFeed should find feed`() {
        val user = signIn()
        val now = Date()
        `when`(feedService.findFeed(any())).doReturn(listOf(TestMicropost.copy(
            content = "content1",
            isMyPost = true,
            createdAt = now,
            user = user.copy(
                username = "test1@test.com"
            )
        )))

        perform(get("/api/feed")).apply {
            andExpect(status().isOk)
            andExpect(jsonPath("$", hasSize<Any>(1)))
            andExpect(jsonPath("$[0].content", `is`("content1")))
            andExpect(jsonPath("$[0].isMyPost", `is`(true)))
            andExpect(jsonPath("$[0].createdAt", greaterThanOrEqualTo(now.time)))
            andExpect(jsonPath("$[0].user.email", nullValue()))
            andExpect(jsonPath("$[0].user.avatarHash", `is`("94fba03762323f286d7c3ca9e001c541")))
        }
    }

    @Test
    fun `findFeed should not find feed when not signed in`() {
        perform(get("/api/feed")).apply {
            andExpect(status().isUnauthorized)
        }
    }
}