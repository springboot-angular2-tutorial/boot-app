package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.UserOptionalParams;
import com.myapp.dto.UserStats;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {

    UserStats getStats(User user);

    User update(User user, UserOptionalParams params);
}
