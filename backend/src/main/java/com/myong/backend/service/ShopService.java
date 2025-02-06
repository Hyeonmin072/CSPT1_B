package com.myong.backend.service;

import com.myong.backend.domain.dto.*;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final DefaultMessageService messageService;
    private final RedisTemplate<String, Object> redisTemplate;

    public HttpStatus shopSignUp(ShopSignUpRequestDto request) {

        try {
            Shop shop = new Shop(
                    request.getName(),
                    request.getPassword(),
                    request.getAddress(),
                    request.getTel(),
                    request.getBizId(),
                    request.getPost()
            ); // 새로운 가게 생성
            shopRepository.save(shop); // 가게를 저장
            log.info("사업자 회원가입 처리 성공 매장명 : {}", request.getName());
            return HttpStatus.OK;
        } catch (Exception e) { // 오류 발생 시
            log.info("사업자 회원가입 처리 중 오류 발생. 매장명 : {}", request.getName());
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    public void sendTelCode(ShopTelRequestDto request) {

    }

    public SingleMessageSentResponse sendOne(ShopTelRequestDto request) {
        Message message = new Message(); // 메시지 객체 생성(외부 라이브러리)
        message.setFrom("01033791271"); // 보낼 전화번호
        message.setTo(request.getTel()); // 받을 전화번호 -> Dto에서 꺼냄

        Random random = new Random(); // 랜덤 인증코드 생성
        int verifyCode = 100000 + random.nextInt(900000); // 100000 ~ 999999사이 랜덤 코드 생성

        message.setText("[Hairism] 인증코드를 입력해주세요 : " + verifyCode); // 메시지 내용 설정

        redisTemplate.opsForValue().set(request.getTel(), verifyCode, 5, TimeUnit.MINUTES); // redis에 키, 값 각각 전화번호, 인증번호 형태로 저장, 5분 시간제한

        return messageService.sendOne(new SingleMessageSendingRequest(message)); // 보내고 난 response 반환
    }

    public HttpStatus checkVerifyCode(ShopVerifyCodeRequestDto request) {
        Integer verifyCode = (Integer) redisTemplate.opsForValue().get(request.getTel());//redis에서 키 값으로 인증번호 꺼내기

        try {
            if (verifyCode == null ) { // 인증코드가 존재하는지 확인
                log.warn("사업자 인증코드가 존재하지 않거나 만료되었습니다. 전화번호 : {}", request.getTel());
                return HttpStatus.UNAUTHORIZED;
            }

            if(verifyCode.equals(request.getVerifyCode())) return HttpStatus.OK; // request의 인증코드가 redis에 저장된 값이랑 같을때
            else return HttpStatus.UNAUTHORIZED; // request의 인증코드가 redis에 저장된 값이랑 인증코드가 다를때
        } catch (Exception e) { // 예외 발생 시
            log.error("사업자 인증코드 확인 중 오류 발생. 전화번호 : {}", request.getTel());
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public HttpStatus checkBiz(ShopBizRequestDto request) {
        return HttpStatus.OK;
    }

    public HttpStatus checkEmail(ShopEmailRequestDto request) {
        Shop findShop = shopRepository.findByEmail(request.getEmail()); // 이메일로 가게 찾기

        try {
            if (findShop == null) return HttpStatus.OK; //null이면 사용가능한 이메일
            else return HttpStatus.CONFLICT; // 이미 있으면 사용 불가능한 이메일
        } catch (Exception e){ // 오류 발생 시
            log.error("이메일 중복 확인 중 오류 발생. 이메일 : {}", request.getEmail());
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
