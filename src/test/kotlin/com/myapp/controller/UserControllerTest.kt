package com.myapp.controller

import com.myapp.domain.UserStats
import com.myapp.dto.page.PageImpl
import com.myapp.dto.request.UserEditParams
import com.myapp.dto.request.UserNewParams
import com.myapp.repository.exception.EmailDuplicatedException
import com.myapp.repository.exception.RecordNotFoundException
import com.myapp.service.UserService
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.Matchers.hasSize
import org.junit.Test
import org.mockito.Mockito.`when`
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
class UserControllerTest : BaseControllerTest() {

    @Suppress("unused")
    private val logger = LoggerFactory.getLogger(UserControllerTest::class.java)

    @MockBean
    lateinit private var userService: UserService

    @Test
    fun `list should list users`() {
        signIn()
        `when`(userService.findAll(101, 1001))
            .doReturn(PageImpl(
                totalPages = 1,
                content = listOf(TestUser.copy(
                    username = "test1@test.com",
                    name = "John Doe"
                ))
            ))

        perform(get("/api/users?page=101&size=1001")).apply {
            andExpect(status().isOk)
            andExpect(jsonPath("$.content").exists())
            andExpect(jsonPath("$.content", hasSize<Any>(1)))
            andExpect(jsonPath("$.content[0].name", `is`("John Doe")))
            andExpect(jsonPath("$.content[0].email", nullValue()))
            andExpect(jsonPath("$.content[0].avatarHash", `is`("94fba03762323f286d7c3ca9e001c541")))
        }
    }

    @Test
    fun `list should not list when not signed in`() {
        perform(get("/api/users")).apply {
            andExpect(status().isUnauthorized)
        }
    }

    @Test
    fun `create should create a user`() {
        `when`(userService.create(any()))
            .doReturn(TestUser.copy(
                _id = 1,
                username = "test1@test.com",
                name = "John Doe"
            ))

        val response = perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj(
                    "email" to "test1@test.com",
                    "password" to "secret123",
                    "name" to "John Doe"
                )
            })
        )

        argumentCaptor<UserNewParams>().apply {
            verify(userService).create(capture())
            assertThat(firstValue.email).isEqualTo("test1@test.com")
            assertThat(firstValue.password).isEqualTo("secret123")
            assertThat(firstValue.name).isEqualTo("John Doe")
        }
        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$.id", `is`(1)))
            andExpect(jsonPath("$.email", nullValue()))
            andExpect(jsonPath("$.name", `is`("John Doe")))
            andExpect(jsonPath("$.avatarHash", `is`("94fba03762323f286d7c3ca9e001c541")))
        }
    }

    @Test
    fun `create should not create a user when password is too short`() {
        perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj(
                    "email" to "test1@test.com",
                    "password" to "a",
                    "name" to "John Doe"
                )
            })
        ).apply {
            andExpect(status().isBadRequest)
        }
    }

    @Test
    fun `create should not create a user when email is duplicated`() {
        `when`(userService.create(any()))
            .doThrow(EmailDuplicatedException(""))

        perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj(
                    "email" to "test1@test.com",
                    "password" to "secret123",
                    "name" to "John Doe"
                )
            })
        ).apply {
            andExpect(status().isBadRequest)
            andExpect(jsonPath("$.code", `is`("email_already_taken")))
        }
    }

    @Test
    fun `show should show a user`() {
        `when`(userService.findOne(1)).doReturn(TestUser.copy(
            name = "John Doe",
            username = "test1@test.com",
            userStats = UserStats(
                followerCnt = 1,
                followingCnt = 2,
                micropostCnt = 3
            )
        ))

        val response = perform(get("/api/users/1"))

        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$.name", `is`("John Doe")))
            andExpect(jsonPath("$.email", nullValue()))
            andExpect(jsonPath("$.avatarHash", `is`("94fba03762323f286d7c3ca9e001c541")))
            andExpect(jsonPath("$.isFollowedByMe", nullValue()))
            andExpect(jsonPath("$.userStats").exists())
            andExpect(jsonPath("$.userStats.micropostCnt", `is`(3)))
            andExpect(jsonPath("$.userStats.followingCnt", `is`(2)))
            andExpect(jsonPath("$.userStats.followerCnt", `is`(1)))
        }
    }

    @Test
    fun `show should not show a user when user not found`() {
        `when`(userService.findOne(1)).doThrow(RecordNotFoundException())

        perform(get("/api/users/1")).apply {
            andExpect(status().isNotFound)
        }
    }

    @Test
    fun `showMe should show me`() {
        signIn()
        `when`(userService.findMe()).doReturn(TestUser.copy(
            name = "John Doe",
            username = "test1@test.com",
            userStats = UserStats(
                followerCnt = 1,
                followingCnt = 2,
                micropostCnt = 3
            )
        ))

        val response = perform(get("/api/users/me"))

        with(response) {
            andExpect(status().isOk)
            andExpect(jsonPath("$.name", `is`("John Doe")))
            andExpect(jsonPath("$.email", nullValue()))
            andExpect(jsonPath("$.avatarHash", `is`("94fba03762323f286d7c3ca9e001c541")))
            andExpect(jsonPath("$.isFollowedByMe", nullValue()))
            andExpect(jsonPath("$.userStats").exists())
            andExpect(jsonPath("$.userStats.micropostCnt", `is`(3)))
            andExpect(jsonPath("$.userStats.followingCnt", `is`(2)))
            andExpect(jsonPath("$.userStats.followerCnt", `is`(1)))
        }
    }

    @Test
    fun `showMe should not show me when not signed in`() {
        perform(get("/api/users/me")).apply {
            andExpect(status().isUnauthorized)
        }
    }

    @Test
    fun `updateMe should update me`() {
        signIn()

        perform(patch("/api/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj(
                    "email" to "test1@test.com",
                    "password" to "secret123",
                    "name" to "John Doe"
                )
            })
        )

        argumentCaptor<UserEditParams>().apply {
            verify(userService).updateMe(capture())
            assertThat(firstValue.email).isEqualTo("test1@test.com")
            assertThat(firstValue.password).isEqualTo("secret123")
            assertThat(firstValue.name).isEqualTo("John Doe")
        }
    }

    @Test
    fun `updateMe should not update me when not signed in`() {
        perform(patch("/api/users/me")).apply {
            andExpect(status().isUnauthorized)
        }
    }

    @Test
    fun `updateMe should not update me when password is too short`() {
        signIn()

        perform(patch("/api/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj(
                    "email" to "test1@test.com",
                    "password" to "a",
                    "name" to "John Doe"
                )
            })
        ).apply {
            andExpect(status().isBadRequest)
        }
    }

}