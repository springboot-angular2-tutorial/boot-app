package com.myapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.domain.User;
import lombok.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Size;
import java.util.Optional;

@Value
public class UserParams {

    private String email;
    @Size(min = 8, max = 100)
    private String password;
    private String name;

    public UserParams(@JsonProperty("email") String email,
                      @JsonProperty("password") String password,
                      @JsonProperty("name") String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User toUser() {
        User user = new User();
        user.setUsername(this.email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setName(name);
        return user;
    }

    public UserOptionalParams toOptionalParams() {
        return new UserOptionalParams(
                Optional.ofNullable(email),
                Optional.ofNullable(password),
                Optional.ofNullable(name)
        );
    }

}
