package com.myapp.auth

import com.myapp.domain.User
import com.myapp.domain.UserDetailsImpl
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SecurityContextServiceImpl : SecurityContextService {

    @Suppress("unused")
    private val logger = LoggerFactory.getLogger(SecurityContextServiceImpl::class.java)

    override fun currentUser(): User? {
        return SecurityContextHolder
            .getContext()
            .authentication
            .principal
            .let {
                when (it) {
                    is UserDetailsImpl -> it.user
                    else -> null
                }
            }
    }

}
