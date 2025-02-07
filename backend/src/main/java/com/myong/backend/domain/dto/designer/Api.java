package com.myong.backend.domain.dto.designer;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Api<T> {
//api에 정보를 각종 dto정보를 담아서 결과코드랑 결과메세지 에러등을 한번에 확인하기위해서 만든 클래스
    private String resultCode; //dto결과코드 ex)200, 400, 404등등

    private String resultMessage; //성공 실패

    @Valid
    private T data; //데이터를 보내기위한 변수

    private ApiError error; // 에러코드

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ApiError {
        private List<String> errorMessage;
    }
}
