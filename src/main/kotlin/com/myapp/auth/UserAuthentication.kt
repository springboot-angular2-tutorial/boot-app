package com.myapp.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserAuthentication(
    private val user: UserDetails
) : Authentication {
    private var authenticated = true

    override fun getAuthorities(): Collection<GrantedAuthority> = user.authorities
    override fun getCredentials(): Any = user.password
    override fun getDetails(): Any? = null
    override fun getPrincipal(): UserDetails = user
    override fun isAuthenticated(): Boolean = authenticated
    override fun getName(): String = user.username

    override fun setAuthenticated(authenticated: Boolean) {
        this.authenticated = authenticated
    }

}
