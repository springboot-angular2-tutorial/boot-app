package com.myapp.dto;

import com.myapp.domain.User;
import lombok.Value;

@Value
public class UserDTO {

    private final User user;
    private final UserStats userStats;

}
