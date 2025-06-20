package com.myong.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BindException extends RuntimeException {
    public BindException(String message) {
        super(message);
    }
}
