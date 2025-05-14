package com.myong.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e, HttpServletRequest request) {
        if (request.getRequestURI().contains("/swagger-ui") || request.getRequestURI().contains("/v3/api-docs")) {
            return ResponseEntity.status(HttpStatus.OK).body("Swagger 관련 요청이므로 예외 처리 생략");
        }
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

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResource(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> ResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }




}
