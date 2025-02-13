package com.myong.backend.jwttoken;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtApi {

    @PostMapping("/signin/request")
    public ResponseEntity<?> SigninRequest(HttpServletRequest request){
        return ResponseEntity.ok("재로그인 성공");
    }
}
