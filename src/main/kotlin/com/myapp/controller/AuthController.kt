package com.myapp.controller

import com.myapp.auth.TokenHandler
import com.myapp.auth.SecurityContextService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val tokenHandler: TokenHandler,
    private val securityContextService: SecurityContextService
) {

    @Suppress("unused")
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping
    fun auth(@RequestBody params: AuthParams): AuthResponse {
        val loginToken = UsernamePasswordAuthenticationToken(params.email, params.password)
        val authentication = authenticationManager.authenticate(loginToken)
        SecurityContextHolder.getContext().authentication = authentication

        return securityContextService.currentUser()
            .let { requireNotNull(it) }
            .let { tokenHandler.createTokenForUser(it).let(::AuthResponse) }
    }

    data class AuthParams(
        val email: String,
        val password: String
    )

    data class AuthResponse(val token: String)

}
