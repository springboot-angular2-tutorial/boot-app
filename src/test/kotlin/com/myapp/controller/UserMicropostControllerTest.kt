package com.myapp.controller

import com.myapp.dto.request.PageParams
import com.myapp.service.MicropostService
import com.myapp.testing.TestMicropost
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.Matchers.hasSize
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserMicropostController::class)
class UserMicropostControllerTest : BaseControllerTest() {

    @MockBean
    lateinit private var micropostService: MicropostService

    @Test
    fun `list should list user's posts`() {
        `when`(micropostService.findAllByUser(eq(1), any()))
            .doReturn(listOf(TestMicropost.copy(
                content = "my content",
                user = TestUser.copy(
                    name = "John Doe"
                )
            )))

        val response = perform(get("/api/users/1/microposts/?sinceId=1&maxId=2&count=3"))

        argumentCaptor<PageParams>().apply {
            verify(micropostService).findAllByUser(eq(1), capture())
            assertThat(firstValue.sinceId).isEqualTo(1)
            assertThat(firstValue.maxId).isEqualTo(2)
            assertThat(firstValue.count).isEqualTo(3)
        }
        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$", hasSize<Any>(1)))
            andExpect(jsonPath("$[0].content", `is`("my content")))
            andExpect(jsonPath("$[0].isMyPost", nullValue()))
            andExpect(jsonPath("$[0].user.name", `is`("John Doe")))
        }
    }

    @Test
    fun `listMyPosts should list my posts`() {
        `when`(micropostService.findMyPosts(any()))
            .doReturn(listOf(TestMicropost.copy(
                content = "my content",
                user = TestUser.copy(
                    name = "John Doe"
                )
            )))

        val response = perform(get("/api/users/me/microposts/?sinceId=1&maxId=2&count=3"))

        argumentCaptor<PageParams>().apply {
            verify(micropostService).findMyPosts(capture())
            assertThat(firstValue.sinceId).isEqualTo(1)
            assertThat(firstValue.maxId).isEqualTo(2)
            assertThat(firstValue.count).isEqualTo(3)
        }
        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$", hasSize<Any>(1)))
            andExpect(jsonPath("$[0].content", `is`("my content")))
            andExpect(jsonPath("$[0].isMyPost", nullValue()))
            andExpect(jsonPath("$[0].user.name", `is`("John Doe")))
        }
    }

    @Test
    fun `listMyPosts should not list my posts when not signed in`() {
        perform(get("/api/users/me/microposts")).apply {
            andExpect(status().isUnauthorized)
        }
    }

}