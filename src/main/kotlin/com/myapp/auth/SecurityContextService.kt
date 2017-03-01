package com.myapp.auth

import com.myapp.domain.User

interface SecurityContextService {
    fun currentUser(): User?
}
