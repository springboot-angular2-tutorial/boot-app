package com.myapp.service;

import com.myapp.domain.User;

public interface SecurityContextService {
    User currentUser();
}
