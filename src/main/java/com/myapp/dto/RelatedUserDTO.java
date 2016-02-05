package com.myapp.dto;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
public class RelatedUserDTO {

    private final User user;

    @Getter
    private final UserStats userStats;

    private final Relationship relationship;

    public long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getUsername();
    }

    public String getName() {
        return user.getName();
    }

    public Long getRelationshipId() {
        return relationship.getId();
    }

}
