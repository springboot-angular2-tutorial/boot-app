package com.myapp.dto;

import com.myapp.domain.User;
import lombok.*;

@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode
public class UserDTO {

    private final User user;

    @Getter
    private final UserStats userStats;

    @Getter
    @Setter
    private Boolean isMyself = null;

    public long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getUsername();
    }

    public String getName() {
        return user.getName();
    }


}
