package com.myapp.dto;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import lombok.*;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Builder
@ToString(exclude = {"user", "relationship"})
@EqualsAndHashCode
public class RelatedUserDTO {

    private final User user;

    @Getter
    private final UserStats userStats;

    private final Relationship relationship;

    @Getter
    @Setter
    private Boolean isMyself = null;

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

    public String getAvatarHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes = MessageDigest.getInstance("MD5")
                .digest(user.getUsername().getBytes("UTF-8"));
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }
}
