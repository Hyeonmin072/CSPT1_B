package com.myong.backend.service;

import com.myong.backend.domain.dto.ShopSignUpRequestDto;
import com.myong.backend.domain.dto.ShopTelRequestDto;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopService {

    private ShopRepository shopRepository;

    public void shopSignUp(ShopSignUpRequestDto request) {
        Shop shop = new Shop(
                request.getName(),
                request.getPassword(),
                request.getAddress(),
                request.getTel(),
                request.getBizId(),
                request.getPost()
        ); // 새로운 가게 생성
        shopRepository.save(shop); // 가게를 저장
    }

    public void sendTelCode(ShopTelRequestDto request) {

    }
}
