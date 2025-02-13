package com.myong.backend.exception;

public class ExistSameEmailException extends RuntimeException {
    public ExistSameEmailException(String message) {
        super(message);
    }
}
