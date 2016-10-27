package com.myapp.auth;

import com.myapp.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface TokenHandler {

    Optional<UserDetails> parseUserFromToken(String token);

    String createTokenForUser(User user);

}
