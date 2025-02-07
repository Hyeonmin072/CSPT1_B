package com.myong.backend.controller;


import com.myong.backend.domain.dto.designer.Api;
import com.myong.backend.domain.dto.designer.SignUpRequest;
import com.myong.backend.service.DesignerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/designer")
public class DesignerController {

    private final DesignerService signUpService;

    @PostMapping("/signup")
    public Api<SignUpRequest>signup(
            @Valid
            @RequestBody
            Api<SignUpRequest> request){
        log.info("signup request: {}", request); //디버깅용 로그찍기

        var body = request.getData();//request의 데이터를 바디에 담고

        signUpService.signUp(body);//서비스에 바디를 넣기

        Api<SignUpRequest> response = Api.<SignUpRequest>builder()
                .resultCode(String.valueOf(HttpStatus.OK.value()))//결과코드가 맞으면 200코드를 반환
                .resultMessage(HttpStatus.OK.getReasonPhrase())//결과코드가 맞으면 ok메세지를 반화
                .data(body)
                .build();

        return response;
    }
}
