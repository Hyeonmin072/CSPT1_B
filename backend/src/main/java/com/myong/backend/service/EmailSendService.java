package com.myong.backend.service;

import com.myong.backend.configuration.RedisConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailSendService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisConfig redisConfig;
    private int authNum;

    //이메일 인증에 필요한 정보
    @Value("${spring.mail.username}")
    private String serviceName;
    @Autowired
    private JavaMailSender javaMailSender;

    //랜덤 인증번호 생성
    public void makeRandomNum(){
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++){
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNum = Integer.parseInt(randomNumber);
    }

    //이메일 전송
    public void mailSend(String setForm, String toMail, String title, String content){
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom("HAIRISM<a3349739@gmail.com>");  //서비스 이름
            helper.setTo(toMail);  //커스텀 이메일
            helper.setSubject(title);  //이메일 타이틀
            helper.setText(content,true); //내용
            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();//에러코드 출력
        }
        //3분동안만 redis에 저장
        ValueOperations<String, String> valueOperations = redisConfig.redisTpl().opsForValue();
        valueOperations.set(toMail, Integer.toString(authNum), 180, TimeUnit.SECONDS);
    }

    //이메일 작성
    public String joinEmail(String email){
        makeRandomNum();
        String customerMail = email;
        String title = "HAIRISM 회원가입을 위한 인증코드입니다.";
        String content =
                "이메일을 인증하기 위한 절차입니다." +
                        "<br><br>" +
                        "인증 번호는 " + authNum + "입니다." +
                        "<br>" +
                        "회원 가입 폼에 해당 번호를 입력해주세요.";
        mailSend(serviceName, customerMail, title, content);
        return Integer.toString(authNum);
    }

    //인증번호 확인
    public Boolean checkAuthNum(String email, String authNum){
        ValueOperations<String, String> valueOperations = redisConfig.redisTpl().opsForValue();
        String code = valueOperations.get(email);

        if(Objects.equals(code, authNum)){
            return true;//코드가 맞으면 true 틀리면 false
        }else
            return false;
    }
}
