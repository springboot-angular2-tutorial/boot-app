package com.myapp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.dto.UserParams;
import com.myapp.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final TokenAuthenticationService tokenAuthenticationService;
    private final UserService userService;

    protected StatelessLoginFilter(String urlMapping,
                                   TokenAuthenticationService tokenAuthenticationService,
                                   UserService userService,
                                   AuthenticationManager authenticationManager) {
        super(urlMapping);
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.userService = userService;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final UserParams params = new ObjectMapper().readValue(request.getInputStream(), UserParams.class);
        final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(params.getEmail(), params.getPassword());
        return getAuthenticationManager().authenticate(loginToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        final UserDetails authenticatedUser = userService.loadUserByUsername(authResult.getName());
        final UserAuthentication userAuthentication = new UserAuthentication(authenticatedUser);

        tokenAuthenticationService.addAuthentication(response, userAuthentication);

        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
    }
}
