package com.myapp.dto;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import lombok.*;

import java.util.Date;

@Builder
@ToString(exclude = {"micropost", "user"})
@EqualsAndHashCode
public class PostDTO {

    private final Micropost micropost;
    private final User user;
    private final UserStats userStats;

    @Setter
    @Getter
    private Boolean isMyPost = null;

    public long getId() {
        return micropost.getId();
    }

    public String getContent() {
        return micropost.getContent();
    }

    public Date getCreatedAt() {
        return micropost.getCreatedAt();
    }

    public UserDTO getUser() {
        return UserDTO.builder()
                .user(user)
                .userStats(userStats)
                .build();
    }
}
