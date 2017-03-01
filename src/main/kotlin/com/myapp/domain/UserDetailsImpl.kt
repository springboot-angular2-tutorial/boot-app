package com.myapp.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(val user: User) : UserDetails {

    override fun getUsername() = user.username
    override fun getPassword() = user.password
    override fun getAuthorities() = mutableListOf(GrantedAuthority { "ROLE_USER" })
    override fun isEnabled() = true
    override fun isCredentialsNonExpired() = true
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true

}