package com.myapp.auth

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class TokenAuthenticationServiceImpl(
    private val tokenHandler: TokenHandler
) : TokenAuthenticationService {

    override fun authentication(request: HttpServletRequest): Authentication? {
        val authHeader = request.getHeader("authorization") ?: return null
        if (!authHeader.startsWith("Bearer")) return null

        val jwt = authHeader.substring(7)
        if (jwt.isEmpty()) return null

        return tokenHandler
            .parseUserFromToken(jwt)
            .let(::UserAuthentication)
    }
}

