package com.myapp.controller

import com.beust.klaxon.JSON
import com.beust.klaxon.JsonObject
import com.beust.klaxon.json
import com.myapp.auth.TokenAuthenticationService
import com.myapp.auth.UserAuthentication
import com.myapp.domain.User
import com.myapp.domain.UserDetailsImpl
import com.myapp.repository.UserRepository
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.ResultActions

@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@ComponentScan(basePackages = arrayOf("com.myapp.auth"))
abstract class BaseControllerTest {

    @Autowired
    lateinit private var mvc: MockMvc

    @MockBean
    lateinit private var tokenAuthenticationService: TokenAuthenticationService

    @MockBean
    lateinit private var userDetailsService: UserDetailsService

    @MockBean
    lateinit private var userRepository: UserRepository

    fun signIn(): User {
        `when`(tokenAuthenticationService.authentication(any()))
            .doReturn(UserAuthentication(UserDetailsImpl(TestUser)))

        return TestUser
    }

    fun perform(requestBuilder: RequestBuilder): ResultActions {
        return mvc.perform(requestBuilder)
    }

    fun jsonString(init: JSON.() -> JsonObject): String {
        return json(init).toJsonString()
    }

}