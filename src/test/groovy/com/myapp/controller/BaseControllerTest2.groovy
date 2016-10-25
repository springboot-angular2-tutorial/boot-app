package com.myapp.controller

import com.myapp.auth.TokenAuthenticationService
import com.myapp.auth.UserAuthentication
import com.myapp.domain.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.core.Authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

@ActiveProfiles("test")
@ComponentScan(basePackages = ["com.myapp.auth"])
class BaseControllerTest2 extends Specification {

    @SuppressWarnings("GroovyUnusedDeclaration")
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MockMvc mockMvc

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService

    ResultActions perform(RequestBuilder requestBuilder) {
        mockMvc.perform(requestBuilder)
    }

    void signIn(User user) {
        Authentication auth = new UserAuthentication(user)
        tokenAuthenticationService.getAuthentication(_ as HttpServletRequest) >> auth
    }
}
