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

        //에러 메세지 리스트 에러를 찾아서 어디서 무슨에러가 발견되었다를 나타냄
        var errorMessageList = exception.getFieldErrors().stream()
                .map( it ->{
                    var format = "%s : { %s } 는 %s"; //에러 폼
                    var message = String.format(format, it.getField(), it.getRejectedValue() , it.getDefaultMessage()); //에러에 들어가는 메세지 내용
                    return message;
                }).collect(Collectors.toList());

        //에러 내용을 담아넣기하는 곳
        var error = Api.ApiError
                .builder()
                .errorMessage(errorMessageList)
                .build();

        //에러코드와 메세지 작성
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
