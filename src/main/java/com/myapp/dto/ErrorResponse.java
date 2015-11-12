package com.myapp.dto;

import lombok.Value;

@Value
public class ErrorResponse {
    private String code;
    private String message;
}
