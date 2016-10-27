package com.myapp.dto;

import lombok.Value;

@Value
public class AuthResponse {
    private final String token;
}
