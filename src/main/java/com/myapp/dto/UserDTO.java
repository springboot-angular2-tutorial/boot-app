package com.myapp.dto;

import com.myapp.domain.User;
import lombok.*;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode
public class UserDTO {

    private final User user;

    @Getter
    private final UserStats userStats;

    @Getter
    @Setter
    private Boolean isMyself = null;

    public long getId() {
        return user.getId();
    }

    public String getEmail() {
        if(isMyself != null && isMyself)
            return user.getUsername();
        else
            return null;
    }

    public String getName() {
        return user.getName();
    }

    public String getAvatarHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes = MessageDigest.getInstance("MD5")
                .digest(user.getUsername().getBytes("UTF-8"));
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

}
