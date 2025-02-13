package com.myong.backend.exception;

import com.myong.backend.controller.ShopController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ShopController.class)
public class ShopExceptionHandler {

    @ExceptionHandler(NotEqualVerifyCodeException.class)
    public ResponseEntity<String> notEqualVerifyCodeException(NotEqualVerifyCodeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(ExistSameEmailException.class)
    public ResponseEntity<String> existSameEmailException(ExistSameEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }


}
