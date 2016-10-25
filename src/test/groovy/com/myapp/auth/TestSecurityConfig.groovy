package com.myapp.auth

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.UserDetailsService
import spock.mock.DetachedMockFactory

@SuppressWarnings("GroovyUnusedDeclaration")
@TestConfiguration
class TestSecurityConfig {

    private DetachedMockFactory factory = new DetachedMockFactory()

    @Bean
    UserDetailsService userDetailsService() {
        factory.Stub(UserDetailsService, name: "userDetailsService")
    }

    @Bean
    TokenAuthenticationService tokenAuthenticationService() {
        factory.Stub(TokenAuthenticationService, name: "tokenAuthenticationService")
    }
}
