package com.myapp.dto;

import com.myapp.domain.UserStats;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class RelatedUserDTO {

    private final long id;
    @NonNull
    private final String name;
    @NonNull
    private final String avatarHash;
    @NonNull
    private final UserStats userStats;
    private final long relationshipId;
    private final Boolean isFollowedByMe;

}
