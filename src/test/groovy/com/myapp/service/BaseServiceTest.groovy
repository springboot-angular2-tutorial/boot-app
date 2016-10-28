package com.myapp.service

import com.myapp.domain.User
import com.myapp.repository.BaseRepositoryTest

abstract class BaseServiceTest extends BaseRepositoryTest {

    SecurityContextService securityContextService = Mock(SecurityContextService)

    Optional<User> currentUser

    def setup() {
        // default not signed in
        currentUser = Optional.empty()
        securityContextService.currentUser() >> { currentUser }
    }

    def signIn(User user) {
        currentUser = Optional.of(user)
    }

    def cleanup() {
        currentUser = Optional.empty()
    }


}
