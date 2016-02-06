package com.myapp.dto;

import lombok.Value;

import java.util.Optional;

@Value
public class UserOptionalParams {
    private final Optional<String> email;
    private final Optional<String> password;
    private final Optional<String> name;
}
