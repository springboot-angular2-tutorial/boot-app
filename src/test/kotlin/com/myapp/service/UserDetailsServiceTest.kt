package com.myapp.service

import com.myapp.repository.UserRepository
import com.myapp.testing.TestUser
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.test.assertFailsWith

class UserDetailsServiceTest {

    private val userRepository: UserRepository = mock()
    private val userDetailsService by lazy {
        UserDetailsServiceImpl(
            userRepository = userRepository
        )
    }

    @Test
    fun `loadUserByUsername should load user by username`() {
        `when`(userRepository.findOneByUsername("test1@test.com"))
            .doReturn(TestUser)

        val userDetails = userDetailsService.loadUserByUsername("test1@test.com")

        assertThat(userDetails).isNotNull()
        assertThat(userDetails.user).isEqualTo(TestUser)
    }

    @Test
    fun `loadUserByUsername should throw when username was not found`() {
        assertFailsWith<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername("test1@test.com")
        }
    }

}