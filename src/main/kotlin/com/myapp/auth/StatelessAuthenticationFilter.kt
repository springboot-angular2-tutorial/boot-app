package com.myapp.auth

import io.jsonwebtoken.JwtException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class StatelessAuthenticationFilter(
    private val tokenAuthenticationService: TokenAuthenticationService
) : GenericFilterBean() {

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        try {
            val authentication = tokenAuthenticationService.authentication(req as HttpServletRequest)
            SecurityContextHolder.getContext().authentication = authentication
            chain.doFilter(req, res)
            SecurityContextHolder.getContext().authentication = null
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()
            (res as HttpServletResponse).status = HttpServletResponse.SC_UNAUTHORIZED
        } catch (e: JwtException) {
            SecurityContextHolder.clearContext()
            (res as HttpServletResponse).status = HttpServletResponse.SC_UNAUTHORIZED
        }
    }

}
