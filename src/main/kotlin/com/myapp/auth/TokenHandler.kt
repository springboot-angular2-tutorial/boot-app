package com.myapp.auth

import com.myapp.domain.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
interface TokenHandler {

    fun parseUserFromToken(token: String): UserDetails
    fun createTokenForUser(user: User): String

}
