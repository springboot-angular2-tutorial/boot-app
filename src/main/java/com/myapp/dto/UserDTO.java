package com.myapp.dto;

import com.myapp.Utils;
import com.myapp.domain.User;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class UserDTO {

    private final long id;
    private final String email;
    private final String name;
    private final String avatarHash;
    private final UserStats userStats;
    private final Boolean isMyself;
    private final Boolean isFollowedByMe;

    public String getEmail() {
        return Optional.ofNullable(isMyself)
                        .filter(Boolean::booleanValue)
                        .map(b -> this.email)
                        .orElse(null);
    }

    public static UserDTO newInstance(User user, UserStats userStats, Boolean isMyself) {
        final String avatarHash = Utils.md5(user.getUsername());

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getUsername())
                .name(user.getName())
                .avatarHash(avatarHash)
                .userStats(userStats)
                .isMyself(isMyself)
                .build();
    }

    public static UserDTO newInstance(User user) {
        return UserDTO.newInstance(user, null, null);
    }

    public static UserDTOBuilder builder2(User user, UserStats userStats) {
        final String avatarHash = Utils.md5(user.getUsername());

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getUsername())
                .name(user.getName())
                .avatarHash(avatarHash)
                .userStats(userStats);
    }

}
