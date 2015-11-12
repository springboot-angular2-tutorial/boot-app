package com.myapp.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserStats {
    private Integer micropostCnt;
    private Integer followingCnt;
    private Integer followerCnt;
}
