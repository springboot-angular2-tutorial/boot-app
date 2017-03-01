package com.myapp.auth

import com.myapp.domain.UserDetailsImpl
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import javax.servlet.http.HttpServletRequest

class TokenAuthenticationServiceTest {

    private val tokenHandler: TokenHandler = mock()
    private val tokenAuthenticationService by lazy {
        TokenAuthenticationServiceImpl(
            tokenHandler = tokenHandler
        )
    }

    @Test
    fun `authentication get authentication from authorization header`() {
        val mockRequest = mock<HttpServletRequest> {
            on { getHeader("authorization") } doReturn "Bearer jwt123"
        }
        val dummyUserDetails = UserDetailsImpl(TestUser)
        `when`(tokenHandler.parseUserFromToken("jwt123")).doReturn(dummyUserDetails)

        val authentication = tokenAuthenticationService.authentication(mockRequest)

        assertThat(authentication).isNotNull()
        assertThat(authentication?.principal).isEqualTo(dummyUserDetails)
    }

    @Test
    fun `authentication returns null when no authorization header`() {
        val mockRequest = mock<HttpServletRequest>()

        val authentication = tokenAuthenticationService.authentication(mockRequest)

        assertThat(authentication).isNull()
    }

}