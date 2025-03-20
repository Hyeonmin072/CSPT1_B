package com.myong.backend.domain.dto.reservation;

import com.myong.backend.domain.entity.business.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateRequestDto {

    @NotBlank
    private LocalDateTime serviceDate; // 서비스 받을 날짜

    @NotBlank
    private PaymentMethod payMethod;  // 결제수단

    @NotBlank
    private String userEmail; //유저 이메일

    @NotBlank
    private String designerEmail; // 디자이너 이메일

    @NotBlank
    private String shopEmail; //가게 이메일

    @NotBlank
    private String couponId; // 쿠폰 아이디

    @NotBlank
    private String menuId; // 메뉴 아이디


}
