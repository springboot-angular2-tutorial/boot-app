package com.myapp.service

import com.myapp.domain.UserDetailsImpl
import com.myapp.repository.UserRepository
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetailsImpl =
        userRepository
            .findOneByUsername(username)
            ?.let(::UserDetailsImpl)
            ?.apply {
                AccountStatusUserDetailsChecker().check(this)
            } ?: throw UsernameNotFoundException("user not found.")

}

