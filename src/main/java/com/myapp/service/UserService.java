package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.UserOptionalParams;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {

    User update(User user, UserOptionalParams params);
}
