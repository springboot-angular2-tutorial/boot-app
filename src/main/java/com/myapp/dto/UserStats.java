package com.myapp.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserStats {
    private final int micropostCnt;
    private final int followingCnt;
    private final int followerCnt;
    private final boolean isFollowedByMe;
}
