package com.myapp.auth

import com.myapp.repository.UserRepository
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import java.util.*

class TokenHandlerTest {

    private val userRepository: UserRepository = mock()
    private val tokenHandler by lazy {
        TokenHandlerImpl(
            secret = "qwerty",
            userRepository = userRepository
        )
    }
    private val ONE_WEEK = 7 * 24 * 60 * 60 * 1000

    @Test
    fun `parseUserFromToken should parse user from a token`() {
        `when`(userRepository.findOne(1))
            .doReturn(TestUser.copy(
                username = "test1@test.com"
            ))

        // This token does not have expiration and has user id = 1
        val jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIn0.Ew-5gopsJtUKofmP-pwDzWvNBCmCd-VIOMNIPaZXVCfn0AUyWmAce2UJ611X35bWiUOFOfuzcz2g6SK2M4Z9Rg"
        val userDetails = tokenHandler.parseUserFromToken(jwt)

        assertThat(userDetails.username).isEqualTo("test1@test.com")
    }


    @Test
    fun `createTokenForUser should create a token for user`() {
        val beginning = Date()
        val token = tokenHandler.createTokenForUser(TestUser.copy(_id = 1))
        val jwt = Jwts.parser()
            .setSigningKey("qwerty")
            .parseClaimsJws(token)
            .body
        val ending = Date()

        assertThat(jwt.subject.toLong()).isEqualTo(1)
        assertThat(jwt.expiration.time)
            // jwt does not have milli secs, so that div(1000).times(1000) is required.
            .isGreaterThanOrEqualTo(beginning.time.div(1000).times(1000) + ONE_WEEK)
            .isLessThanOrEqualTo(ending.time.div(1000).times(1000) + ONE_WEEK)
    }
}