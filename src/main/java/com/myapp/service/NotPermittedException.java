package com.myapp.service;

public class NotPermittedException extends Exception {

    NotPermittedException(String message) {
        super(message);
    }

}
