package com.myong.backend.controller;

import com.myong.backend.domain.dto.ShopSignUpRequestDto;
import com.myong.backend.domain.dto.ShopTelRequestDto;
import com.myong.backend.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.service.MessageService;
import org.apache.juli.VerbatimFormatter;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {
    private final ShopService shopService;
    private final DefaultMessageService messageService;


    /**
     * 사업자 회원가입
     */
    @PostMapping("/signup")
    public void shopSignUp(@RequestBody ShopSignUpRequestDto request) {
        shopService.shopSignUp(request);
    }


    /**
     * 사업자 전화번호 인증코드 보내기
     */
    @PostMapping("/send-one")
    public SingleMessageSentResponse sendOne() {
        Message message = new Message();
        message.setFrom("01033791271");
        message.setTo("01086465788");

        Random random = new Random();
        int verifyCode = 100000 + random.nextInt(900000);

        message.setText("이 바보야 : " + verifyCode);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        return response;
    }

    /**
     * 사업자 전화번호 인증코드 확인하기
     */


    /**
     * 사업자번호 인증
     */

}
