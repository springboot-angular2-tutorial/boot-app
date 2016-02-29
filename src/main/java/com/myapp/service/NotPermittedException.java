package com.myapp.service;

public class NotPermittedException extends RuntimeException {

    NotPermittedException(String message) {
        super(message);
    }

}
