package com.myapp.controller

import com.myapp.dto.request.PageParams
import com.myapp.service.RelatedUserService
import com.myapp.testing.TestRelatedUser
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RelatedUserController::class)
class RelatedUserControllerTest : BaseControllerTest() {

    @MockBean
    lateinit private var relatedUserService: RelatedUserService

    @Test
    fun `followings should list followings`() {
        `when`(relatedUserService.findFollowings(eq(1), any()))
            .doReturn(listOf(TestRelatedUser.copy(
                name = "John Doe",
                username = "test1@test.com",
                relationshipId = 101
            )))

        val response = perform(get("/api/users/1/followings?sinceId=1&maxId=2&count=3"))

        argumentCaptor<PageParams>().apply {
            verify(relatedUserService).findFollowings(eq(1), capture())
            assertThat(firstValue.sinceId).isEqualTo(1)
            assertThat(firstValue.maxId).isEqualTo(2)
            assertThat(firstValue.count).isEqualTo(3)
        }
        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$[0].name", `is`("John Doe")))
            andExpect(jsonPath("$[0].avatarHash", `is`("94fba03762323f286d7c3ca9e001c541")))
            andExpect(jsonPath("$[0].relationshipId", `is`(101)))
        }
    }

    @Test
    fun `followers should list followers`() {
        `when`(relatedUserService.findFollowers(eq(1), any()))
            .doReturn(listOf(TestRelatedUser.copy(
                name = "John Doe",
                username = "test1@test.com",
                relationshipId = 101
            )))

        val response = perform(get("/api/users/1/followers?sinceId=1&maxId=2&count=3"))

        argumentCaptor<PageParams>().apply {
            verify(relatedUserService).findFollowers(eq(1), capture())
            assertThat(firstValue.sinceId).isEqualTo(1)
            assertThat(firstValue.maxId).isEqualTo(2)
            assertThat(firstValue.count).isEqualTo(3)
        }
        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$[0].name", `is`("John Doe")))
            andExpect(jsonPath("$[0].avatarHash", `is`("94fba03762323f286d7c3ca9e001c541")))
            andExpect(jsonPath("$[0].relationshipId", `is`(101)))
        }
    }

}