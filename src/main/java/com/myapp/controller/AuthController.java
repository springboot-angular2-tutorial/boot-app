package com.myapp.controller;

import com.myapp.auth.TokenHandler;
import com.myapp.dto.UserParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by riccardo.causo on 17.10.2016.
 */
@RestController
@RequestMapping("/api/login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenHandler tokenHandler;
    private final UserDetailsService userDetailsService;

    @Autowired
    AuthController(
            AuthenticationManager authenticationManager,
            TokenHandler tokenHandler,
            UserDetailsService userDetailsService
    ){

        this.authenticationManager = authenticationManager;
        this.tokenHandler = tokenHandler;
        this.userDetailsService = userDetailsService;
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> authenticationRequest(@RequestBody UserParams params) throws AuthenticationException {

        UsernamePasswordAuthenticationToken loginToken = params.toAuthenticationToken();
        Authentication authentication =  authenticationManager.authenticate(loginToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(params.getEmail().get());

        String token = tokenHandler.createTokenForUser(userDetails);

        // Return the token
        return ResponseEntity.ok().header("x-auth-token",token).body("");
    }


}
