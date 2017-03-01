package com.myapp.service

import com.myapp.auth.SecurityContextService
import com.myapp.domain.User
import org.springframework.security.access.AccessDeniedException

interface WithCurrentUser {

    val securityContextService: SecurityContextService

    fun currentUser(): User? =
        securityContextService.currentUser()

    fun currentUserOrThrow(): User =
        securityContextService.currentUser() ?: throw AccessDeniedException("")
}