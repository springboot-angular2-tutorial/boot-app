package com.myapp.dto;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
public class PostDTO {

    private final Micropost micropost;
    private final User user;
    private final UserStats userStats;

    public long getId() {
        return micropost.getId();
    }

    public String getContent() {
        return micropost.getContent();
    }

    public UserDTO getUser() {
        return UserDTO.builder()
                .user(user)
                .userStats(userStats)
                .build();
    }
}
