package com.myapp.dto;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import lombok.Builder;

@Builder
public class RelatedUserDTO {

    private final User user;
    private final UserStats userStats;
    private final Relationship relationship;

    public Long getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getUsername();
    }

    public String getName() {
        return user.getName();
    }

    public UserStats getUserStats() {
        return userStats;
    }

    public Long getRelationshipId() {
        return relationship.getId();
    }


}
