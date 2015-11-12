package com.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@AllArgsConstructor
public class UserOptionalParams {
    private Optional<String> email;
    private Optional<String> password;
    private Optional<String> name;
}
