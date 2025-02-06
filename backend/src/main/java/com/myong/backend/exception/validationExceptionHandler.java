package com.myong.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.myong.backend.domain.dto.designer.Api;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class validationExceptionHandler {
    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<Api> validException(
            MethodArgumentNotValidException exception
    ){
        log.error("", exception);

        var errorMessageList = exception.getFieldErrors().stream()
                .map( it ->{
                    var format = "%s : { %s } 는 %s";
                    var message = String.format(format, it.getField(), it.getRejectedValue() , it.getDefaultMessage());
                    return message;
                }).collect(Collectors.toList());

        var error = Api.ApiError
                .builder()
                .errorMessage(errorMessageList)
                .build();

        var errorResponse = Api
                .builder()
                .resultCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .resultMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .error(error)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);

    }
}
