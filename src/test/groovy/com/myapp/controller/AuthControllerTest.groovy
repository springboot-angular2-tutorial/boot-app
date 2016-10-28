package com.myapp.controller

import com.myapp.auth.TokenHandler
import com.myapp.auth.UserAuthentication
import com.myapp.domain.User
import com.myapp.service.SecurityContextService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import spock.mock.DetachedMockFactory

import static groovy.json.JsonOutput.toJson
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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
        TokenHandler tokenHandler(DetachedMockFactory f) {
            return f.Mock(TokenHandler)
        }

        @Bean
        SecurityContextService securityContextService(DetachedMockFactory f) {
            return f.Mock(SecurityContextService)
        }
    }

    @Autowired
    AuthenticationManager authenticationManager

    @Autowired
    TokenHandler tokenHandler

    @Autowired
    SecurityContextService securityContextService

    def setup() {
        User user = new User(id: 1, username: "test1@test.com", password: "secret", name: "test1")
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken("test1@test.com", "secret");
        authenticationManager.authenticate(loginToken) >> new UserAuthentication(user)
        authenticationManager.authenticate(_ as Authentication) >> {
            throw new BadCredentialsException("")
        }
        securityContextService.currentUser() >> Optional.of(user)
        tokenHandler.createTokenForUser(user) >> "created jwt"
    }

    def "can auth when username and password are correct"() {
        when:
        def response = perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: "test1@test.com", password: "secret"))
        )

        then:
        with(response) {
            andExpect(status().isOk())
            andExpect(jsonPath('$.token', is("created jwt")))
        }
    }

    def "can not auth when username or password is not correct"() {
        when:
        def response = perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(email: "test2@test.com", password: "secret"))
        )

        then:
        with(response) {
            andExpect(status().isUnauthorized())
        }
    }

}

