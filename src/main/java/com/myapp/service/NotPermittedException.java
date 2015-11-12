package com.myapp.service;

public class NotPermittedException extends RuntimeException {

    public NotPermittedException(String message) {
        super(message);
    }

}
