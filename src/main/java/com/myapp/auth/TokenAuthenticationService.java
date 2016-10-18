package com.myapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
class TokenAuthenticationService {

    private static final String AUTH_HEADER_NAME = "x-auth-token";

    private final TokenHandler tokenHandler;

    @Autowired
    public TokenAuthenticationService(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token == null || token.isEmpty()) return null;
        return tokenHandler
                .parseUserFromToken(token)
                .map(UserAuthentication::new)
                .orElse(null);
    }
}

