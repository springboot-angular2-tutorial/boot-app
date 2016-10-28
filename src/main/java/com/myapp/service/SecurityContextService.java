package com.myapp.service;

import com.myapp.domain.User;

import java.util.Optional;

public interface SecurityContextService {
    Optional<User> currentUser();
}
