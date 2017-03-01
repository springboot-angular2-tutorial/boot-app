package com.myapp.controller

import com.myapp.auth.TokenHandler
import com.myapp.domain.UserDetailsImpl
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.doReturn
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController::class)
class AuthControllerTest : BaseControllerTest() {

    @MockBean
    lateinit private var tokenHandler: TokenHandler

    @Autowired
    lateinit private var userDetailsService: UserDetailsService

    @Test
    fun `auth should auth`() {
        val userInDb = TestUser.copy(
            username = "test1@test.com",
            password = BCryptPasswordEncoder().encode("secret123")
        )
        `when`(userDetailsService.loadUserByUsername("test1@test.com"))
            .doReturn(UserDetailsImpl(userInDb))
        `when`(tokenHandler.createTokenForUser(userInDb))
            .doReturn("dummy token")

        perform(post("/api/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj(
                    "email" to "test1@test.com",
                    "password" to "secret123"
                )
            })
        ).apply {
            andExpect(status().isOk)
            andExpect(jsonPath("$.token", `is`("dummy token")))
        }
    }

    @Test
    fun `auth should not auth when email and password is not valid`() {
        val userInDb = TestUser.copy(
            username = "test1@test.com",
            password = BCryptPasswordEncoder().encode("secret123")
        )
        `when`(userDetailsService.loadUserByUsername("test1@test.com"))
            .doReturn(UserDetailsImpl(userInDb))

        perform(post("/api/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString {
                obj(
                    "email" to "test2@test.com",
                    "password" to "secret123"
                )
            })
        ).apply {
            andExpect(status().isUnauthorized)
        }
    }

}