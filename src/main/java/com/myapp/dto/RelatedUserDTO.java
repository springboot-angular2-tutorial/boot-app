package com.myapp.dto;

import com.myapp.Utils;
import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class RelatedUserDTO {

    private final long id;
    @NonNull
    private final String email;
    @NonNull
    private final String name;
    @NonNull
    private final String avatarHash;
    private final UserStats userStats;
    private final long relationshipId;
    private final Boolean isMyself;
    private final Boolean isFollowedByMe;

    public static RelatedUserDTOBuilder builder2(User user, Relationship relationship, UserStats userStats) {
        final String avatarHash = Utils.md5(user.getUsername());

        return RelatedUserDTO.builder()
                .id(user.getId())
                .email(user.getUsername())
                .name(user.getName())
                .avatarHash(avatarHash)
                .userStats(userStats)
                .relationshipId(relationship.getId());
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return Optional.ofNullable(isMyself)
                .filter(Boolean::booleanValue)
                .map(b -> email)
                .orElse(null);
    }

}
