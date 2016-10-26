package com.myapp.dto;

import com.myapp.Utils;
import com.myapp.domain.User;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class UserDTO {

    private final long id;
    @NonNull
    private final String email;
    @NonNull
    private final String name;
    @NonNull
    private final String avatarHash;
    private final UserStats userStats;
    private final Boolean isMyself;
    private final Boolean isFollowedByMe;

    public static UserDTO newInstance(User user) {
        return UserDTO.builder2(user, null).build();
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

    @SuppressWarnings("unused")
    public String getEmail() {
        return Optional.ofNullable(isMyself)
                .filter(Boolean::booleanValue)
                .map(b -> email)
                .orElse(null);
    }

}
