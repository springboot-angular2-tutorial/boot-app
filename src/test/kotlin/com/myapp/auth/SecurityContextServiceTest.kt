package com.myapp.auth

import com.myapp.domain.UserDetailsImpl
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class SecurityContextServiceTest {

    private val securityContextService = SecurityContextServiceImpl()

    lateinit private var securityContext: SecurityContext

    @Before
    fun storeContext() {
        securityContext = SecurityContextHolder.getContext()
    }

    @Test
    fun `currentUser should return current user`() {
        val userDetails = UserDetailsImpl(TestUser)
        SecurityContextHolder.setContext(mock {
            on { authentication } doReturn UserAuthentication(userDetails)
        })

        val currentUser = securityContextService.currentUser()

        assertThat(currentUser).isNotNull()
        assertThat(currentUser).isEqualTo(TestUser)
    }

    @Test
    fun `currentUser should return null when not signed in`() {
        SecurityContextHolder.setContext(mock {
            on { authentication } doReturn mock<Authentication>()
        })

        val currentUser = securityContextService.currentUser()

        assertThat(currentUser).isNull()
    }

    @After
    fun restoreContext() {
        SecurityContextHolder.setContext(securityContext)
    }

}

