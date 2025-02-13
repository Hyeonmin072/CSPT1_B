package com.myong.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("내부서버 오류가 발생했습니다.\n발생 이유 : " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runTimeExceptionHandler (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullPointerExceptionHandler(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부서버에서 null 예외가 발생했습니다.\n발생 이유 : " + e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> bindException(BindException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 클라이언트 요청입니다.\n발생 이유 : " + e.getMessage());
    }


}
