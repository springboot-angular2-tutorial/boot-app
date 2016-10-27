package com.myapp.auth

import com.myapp.repository.UserRepository
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
        factory.Stub(UserDetailsService)
    }

    @Bean
    TokenAuthenticationService tokenAuthenticationService() {
        factory.Stub(TokenAuthenticationService)
    }

    @Bean
    UserRepository userRepository() {
        factory.Stub(UserRepository)
    }
}
