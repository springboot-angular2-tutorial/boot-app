package com.myapp.controller

import com.myapp.auth.TokenAuthenticationService
import com.myapp.auth.UserAuthentication
import com.myapp.domain.User
import com.myapp.service.RelationshipService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.core.Authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import javax.servlet.http.HttpServletRequest

@ActiveProfiles("test")
@ComponentScan(basePackages = ["com.myapp.auth"])
abstract class BaseControllerTest extends Specification {

    @SuppressWarnings("GroovyUnusedDeclaration")
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @TestConfiguration
    static class Config {
        @Bean
        DetachedMockFactory detachedMockFactory() {
            return new DetachedMockFactory()
        }
    }

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService

    ResultActions perform(RequestBuilder requestBuilder) {
        return mockMvc.perform(requestBuilder)
    }

    User signIn(User user) {
        Authentication auth = new UserAuthentication(user)
        tokenAuthenticationService.getAuthentication(_ as HttpServletRequest) >> auth
        return user
    }

    User signIn() {
        User user = new User(id: 1, username: "test@test.com", password: "secret", name: "test")
        Authentication auth = new UserAuthentication(user)
        tokenAuthenticationService.getAuthentication(_ as HttpServletRequest) >> auth
        return user
    }
}
