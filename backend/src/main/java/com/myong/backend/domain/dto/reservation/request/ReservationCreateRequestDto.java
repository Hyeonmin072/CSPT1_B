package com.myong.backend.domain.dto.reservation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateRequestDto {

    @NotBlank
    private LocalDateTime serviceDate; // 서비스 받을 날짜1

    @NotNull
    private String designerEmail; // 디자이너 이메일

    @NotNull
    private String shopEmail; //가게 이메일

    @NotBlank
    private String couponId; // 쿠폰 아이디

    @NotBlank
    private String menuId; // 메뉴 아이디

    @NotNull
    private Integer price; // 최종 금액


}
