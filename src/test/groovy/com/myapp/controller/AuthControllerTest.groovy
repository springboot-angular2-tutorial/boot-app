package com.myapp.controller

import com.myapp.auth.TokenHandler
import com.myapp.auth.TokenHandlerImpl
import com.myapp.auth.UserAuthentication
import com.myapp.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import spock.mock.DetachedMockFactory

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.isEmptyOrNullString
import static org.hamcrest.Matchers.not
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthController)
class AuthControllerTest extends BaseControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        AuthenticationManager authenticationManager(DetachedMockFactory f) {
            return f.Mock(AuthenticationManager)
        }

        @Bean
        UserDetailsService userDetailsService(DetachedMockFactory f) {
            return f.Mock(UserDetailsService)
        }

        @Bean
        TokenHandler tokenHandler(UserDetailsService userService) {
            return new TokenHandlerImpl("jwt secret", userService)
        }
    }

    @Autowired
    AuthenticationManager authenticationManager

    @Autowired
    UserDetailsService userDetailsService

    @Autowired
    TokenHandler tokenHandler

    def setup() {
        User user = new User(id: 1, username: "test1@test.com", password: "secret", name: "test1")
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken("test1@test.com", "secret");
        authenticationManager.authenticate(loginToken) >> new UserAuthentication(user)
        authenticationManager.authenticate(_ as Authentication) >> {
            throw new BadCredentialsException("")
        }
        userDetailsService.loadUserByUsername("test1@test.com") >> user
    }

    def "can auth when username and password are correct"() {
        when:
        def response = perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: "test1@test.com", password: "secret"))
        )

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(header().string("x-auth-token", not(isEmptyOrNullString())))
        }
    }

    def "can not auth when username or password is not correct"() {
        when:
        def response = perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: "test2@test.com", password: "secret"))
        )

        then:
        with(response) {
            andExpect(status().isUnauthorized())
        }
    }

}

