package com.myapp.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class UserStats {
    private final long micropostCnt;
    private final long followingCnt;
    private final long followerCnt;
    private final boolean isFollowedByMe;

    @QueryProjection
    public UserStats(long micropostCnt, long followingCnt, long followerCnt, long isFollowedByMe) {
        this.micropostCnt = micropostCnt;
        this.followingCnt = followingCnt;
        this.followerCnt = followerCnt;
        this.isFollowedByMe = isFollowedByMe == 1;
    }
}
