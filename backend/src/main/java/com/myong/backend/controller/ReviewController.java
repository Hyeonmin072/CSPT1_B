package com.myong.backend.controller;

import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.service.ReviewService;
import com.myong.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/register")
    public ResponseEntity<String> registerReview(@RequestBody ShopRegisterReviewRequestDto request){
        return ResponseEntity.ok(reviewService.registerReview(request));
    }
}
